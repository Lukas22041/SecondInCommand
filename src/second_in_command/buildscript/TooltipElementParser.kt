package second_in_command.buildscript

import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import second_in_command.misc.getChildrenCopy
import java.awt.Color

/**
 * Correlates recorded proxy calls with the rendered child tree to build TooltipElement lists.
 *
 * Proxy records tell us what was called and with what arguments (highlights, padding, sprite paths).
 * Rendered children tell us the final text, color, and position (via stable public APIs).
 */
object TooltipElementParser {

    // Methods on TooltipMakerAPI that change the font but don't create a child element
    private val FONT_METHODS = setOf(
        "setParaFont", "setParaFontDefault", "setParaSmallInsignia",
        "setParaFontVictor14", "setParaSmallOrbitron", "setParaFontOrbitron",
        "setParaOrbitronLarge", "setParaOrbitronVeryLarge",
        "setParaInsigniaLarge", "setParaInsigniaVeryLarge"
    )

    // Methods that don't create children and should be skipped
    private val SKIP_METHODS = setOf(
        "codexEntryId", "setCodexEntryId",
        "setParaFont", "setParaFontDefault", "setParaSmallInsignia",
        "setParaFontVictor14", "setParaSmallOrbitron", "setParaFontOrbitron",
        "setParaOrbitronLarge", "setParaOrbitronVeryLarge",
        "setParaInsigniaLarge", "setParaInsigniaVeryLarge"
    )

    fun parse(
        recordedCalls: List<RecordedCall>,
        realTooltip: TooltipMakerAPI
    ): List<TooltipElement> {
        val children = (realTooltip as UIPanelAPI).getChildrenCopy()
        val elements = mutableListOf<TooltipElement>()
        var childIndex = 0
        var currentFont: String? = null

        for (call in recordedCalls) {
            when {
                call.methodName == "addTitle" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    elements.add(parseLabelFromCall(call, child, currentFont, isTitle = true))
                }

                call.methodName == "addPara" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    elements.add(parseLabelFromCall(call, child, currentFont))
                }

                call.methodName == "addSpacer" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    val height = (call.args[0] as Number).toFloat()
                    elements.add(SpacerElement(
                        height = height,
                        position = extractPosition(child)
                    ))
                }

                call.methodName == "beginImageWithText" -> {
                    // Don't increment child index — the child is created by addImageWithText
                }

                call.methodName == "addImageWithText" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    elements.add(parseImageWithText(call, child))
                }

                call.methodName == "addSectionHeading" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    elements.add(parseSectionHeading(call, child))
                }

                call.methodName == "addImage" || call.methodName == "addImages" -> {
                    val child = children.getOrNull(childIndex++) ?: continue
                    elements.add(parseImage(call, child))
                }

                call.methodName in FONT_METHODS -> {
                    currentFont = call.methodName
                    // No child created
                }

                // For any setter or non-child-creating method, skip
                call.methodName.startsWith("set") || call.methodName in SKIP_METHODS -> {
                    // No child created
                }

                else -> {
                    // Unknown method that might create a child — we can't know for sure.
                    // Log but don't skip a child index to avoid desync.
                }
            }
        }

        return elements
    }

    private fun parseLabelFromCall(
        call: RecordedCall,
        child: UIComponentAPI,
        font: String?,
        isTitle: Boolean = false
    ): LabelElement {
        // Get label from the recorded result (the LabelAPI returned by the real call)
        // The child in the panel may be a wrapper panel, not the LabelAPI directly
        val label = call.result as LabelAPI
        val text = label.text ?: ""
        val baseColor = label.color ?: Color.WHITE
        val position = extractPosition(child)

        // FROM PROXY RECORDING:
        val padding = extractPadding(call)
        val highlights = buildHighlightRanges(call, text)

        return LabelElement(
            text = text,
            color = ColorData.fromColor(baseColor),
            highlightRanges = highlights,
            font = font,
            isTitle = isTitle,
            padding = padding,
            position = position
        )
    }

    private fun parseImageWithText(call: RecordedCall, child: UIComponentAPI): ImageWithTextElement {
        val beginCall = call.pairedBeginCall
        val spriteName = beginCall?.args?.getOrNull(0) as? String ?: ""
        val imageHeight = (beginCall?.args?.getOrNull(1) as? Number)?.toFloat() ?: 48f
        val padding = extractPadding(call)

        // Parse inner elements from the inner recorder
        val innerElements = if (beginCall?.innerRecorder != null && beginCall.innerRealTooltip != null) {
            parse(beginCall.innerRecorder!!.recordedCalls, beginCall.innerRealTooltip!!)
        } else {
            emptyList()
        }

        return ImageWithTextElement(
            spriteName = spriteName,
            assetFileName = spriteToAssetName(spriteName),
            imageHeight = imageHeight,
            padding = padding,
            children = innerElements,
            position = extractPosition(child)
        )
    }

    private fun parseSectionHeading(call: RecordedCall, child: UIComponentAPI): SectionHeadingElement {
        val args = call.args
        // Overloads:
        // addSectionHeading(String, Alignment, float pad)
        // addSectionHeading(String, Color textColor, Color bgColor, Alignment, float pad)
        val text = args.getOrNull(0) as? String ?: ""
        var textColor = Color.WHITE
        var bgColor = Color.DARK_GRAY
        var alignment = "MID"
        var padding = 0f

        if (args.size >= 5) {
            // 5-arg version: (String, Color, Color, Alignment, float)
            textColor = args[1] as? Color ?: Color.WHITE
            bgColor = args[2] as? Color ?: Color.DARK_GRAY
            alignment = args[3]?.toString() ?: "MID"
            padding = (args[4] as? Number)?.toFloat() ?: 0f
        } else if (args.size >= 3) {
            // 3-arg version: (String, Alignment, float)
            alignment = args[1]?.toString() ?: "MID"
            padding = (args[2] as? Number)?.toFloat() ?: 0f
        }

        return SectionHeadingElement(
            text = text,
            textColor = ColorData.fromColor(textColor),
            bgColor = ColorData.fromColor(bgColor),
            alignment = alignment,
            padding = padding,
            position = extractPosition(child)
        )
    }

    private fun parseImage(call: RecordedCall, child: UIComponentAPI): ImageElement {
        // addImage(String spriteName, float width, float height, float pad)
        // or addImage(String spriteName, float pad)
        val spriteName = call.args.getOrNull(0) as? String ?: ""
        var width = 0f
        var height = 0f
        var padding = 0f

        if (call.args.size >= 4) {
            width = (call.args[1] as? Number)?.toFloat() ?: 0f
            height = (call.args[2] as? Number)?.toFloat() ?: 0f
            padding = (call.args[3] as? Number)?.toFloat() ?: 0f
        } else if (call.args.size >= 2) {
            padding = (call.args[1] as? Number)?.toFloat() ?: 0f
        }

        val pos = extractPosition(child)
        if (width == 0f) width = pos.width
        if (height == 0f) height = pos.height

        return ImageElement(
            spriteName = spriteName,
            assetFileName = spriteToAssetName(spriteName),
            width = width,
            height = height,
            padding = padding,
            position = pos
        )
    }

    /**
     * Build highlight ranges by combining proxy-recorded highlight info with the rendered text.
     */
    private fun buildHighlightRanges(call: RecordedCall, text: String): List<HighlightRange> {
        val ranges = mutableListOf<HighlightRange>()

        // Extract initial highlight strings and color from addPara args
        var highlightStrings = extractHighlightStringsFromArgs(call)
        var defaultHighlightColor = extractHighlightColorFromArgs(call)
        var perHighlightColors: Array<Color>? = extractPerHighlightColorsFromArgs(call)

        // Check for subsequent label calls that override highlights
        for (labelCall in call.labelCalls) {
            when (labelCall.methodName) {
                "setHighlight" -> {
                    // setHighlight(String... substrings) — overrides addPara highlights
                    highlightStrings = labelCall.args.filterIsInstance<String>().toList()
                    // Handle varargs — could be passed as String[] array
                    if (highlightStrings.isEmpty() && labelCall.args.isNotEmpty()) {
                        val first = labelCall.args[0]
                        if (first is Array<*>) {
                            highlightStrings = first.filterIsInstance<String>()
                        }
                    }
                }
                "setHighlightColor" -> {
                    // Single color override for all highlights
                    val color = labelCall.args.getOrNull(0) as? Color
                    if (color != null) defaultHighlightColor = color
                }
                "setHighlightColors" -> {
                    // Per-highlight colors: setHighlightColors(Color... colors)
                    val first = labelCall.args.getOrNull(0)
                    if (first is Array<*>) {
                        @Suppress("UNCHECKED_CAST")
                        perHighlightColors = first as? Array<Color>
                    }
                }
            }
        }

        // Build ranges by finding highlight strings in the rendered text
        var searchFrom = 0
        for ((i, hlStr) in highlightStrings.withIndex()) {
            if (hlStr.isEmpty()) continue
            val startIdx = text.indexOf(hlStr, searchFrom)
            if (startIdx >= 0) {
                val color = when {
                    perHighlightColors != null && i < perHighlightColors.size -> perHighlightColors[i]
                    defaultHighlightColor != null -> defaultHighlightColor
                    else -> Color.YELLOW
                }
                ranges.add(HighlightRange(
                    startIndex = startIdx,
                    endIndex = startIdx + hlStr.length,
                    text = hlStr,
                    color = ColorData.fromColor(color)
                ))
                searchFrom = startIdx + hlStr.length
            }
        }

        return ranges
    }

    /**
     * Extract highlight strings from addPara varargs.
     * addPara(String format, float pad, Color hl, String... highlights)
     * addPara(String format, float pad, Color textColor, Color hl, String... highlights)
     */
    /**
     * Extract highlight strings from addPara args.
     * Args are stored as explicit lists by RecordingTooltipMaker:
     *   addPara(str, pad)                          → [str, pad]
     *   addPara(format, pad, hlColor, highlights)  → [format, pad, Color, Array<String>]
     *   addPara(str, color, pad)                   → [str, Color, pad]
     *   addPara(format, pad, color, hl, highlights)→ [format, pad, Color, Color, Array<String>]
     *   addPara(format, pad, hlColors, highlights) → [format, pad, Array<Color>, Array<String>]
     */
    private fun extractHighlightStringsFromArgs(call: RecordedCall): List<String> {
        if (call.methodName != "addPara") return emptyList()
        val args = call.args
        if (args.size <= 2) return emptyList()

        // Count Color-like args after the float (index 2+), skipping Color arrays too
        var colorCount = 0
        for (i in 2 until args.size) {
            val arg = args[i]
            if (arg is Color || (arg is Array<*> && arg.isNotEmpty() && arg[0] is Color)) {
                colorCount++
            } else {
                break
            }
        }

        val highlightStart = 2 + colorCount
        val highlights = mutableListOf<String>()
        for (i in highlightStart until args.size) {
            val arg = args[i]
            if (arg is String) {
                highlights.add(arg)
            } else if (arg is Array<*>) {
                highlights.addAll(arg.filterIsInstance<String>())
            }
        }
        return highlights
    }

    /**
     * Extract the highlight color(s) from addPara args.
     * With 1 Color arg: it's the highlight color.
     * With 2 Color args: first is text color, second is highlight color.
     * With Color[] arg: returns first color (per-highlight colors handled separately).
     */
    private fun extractHighlightColorFromArgs(call: RecordedCall): Color? {
        if (call.methodName != "addPara") return null
        val args = call.args
        if (args.size <= 2) return null

        // Check for Color[] variant: addPara(format, pad, Color[], String...)
        val arg2 = args[2]
        if (arg2 is Array<*> && arg2.isNotEmpty() && arg2[0] is Color) {
            return arg2[0] as Color // first color as default
        }

        val colors = mutableListOf<Color>()
        for (i in 2 until args.size) {
            if (args[i] is Color) colors.add(args[i] as Color) else break
        }

        return when (colors.size) {
            1 -> colors[0]     // highlight color
            2 -> colors[1]     // second is highlight color
            else -> null
        }
    }

    /**
     * Extract per-highlight colors from the Color[] addPara variant.
     */
    private fun extractPerHighlightColorsFromArgs(call: RecordedCall): Array<Color>? {
        if (call.methodName != "addPara") return null
        val args = call.args
        if (args.size <= 2) return null

        val arg2 = args[2]
        if (arg2 is Array<*> && arg2.isNotEmpty() && arg2[0] is Color) {
            @Suppress("UNCHECKED_CAST")
            return arg2 as Array<Color>
        }
        return null
    }

    /**
     * Extract padding (float) from call args.
     * For addPara: arg index 1
     * For addSpacer: arg index 0
     * For addImageWithText: arg index 0
     * For addTitle: may not have padding
     */
    private fun extractPadding(call: RecordedCall): Float {
        return when (call.methodName) {
            "addPara" -> (call.args.getOrNull(1) as? Number)?.toFloat() ?: 0f
            "addSpacer" -> (call.args.getOrNull(0) as? Number)?.toFloat() ?: 0f
            "addImageWithText" -> (call.args.getOrNull(0) as? Number)?.toFloat() ?: 0f
            "addTitle" -> 0f // addTitle(String) or addTitle(String, Color) — no padding param
            else -> 0f
        }
    }

    fun extractPosition(component: UIComponentAPI): PositionData {
        val pos = component.position
        return PositionData(
            x = pos.x,
            y = pos.y,
            width = pos.width,
            height = pos.height
        )
    }

    /**
     * Convert a sprite path like "graphics/icons/abilities/sc_re_evaluate.png"
     * to a flat asset filename like "sc_re_evaluate.png".
     * If collision potential, prefix with parent dir.
     */
    fun spriteToAssetName(spritePath: String): String {
        if (spritePath.isEmpty()) return ""
        val name = spritePath.substringAfterLast("/").substringAfterLast("\\")
        return name
    }
}
