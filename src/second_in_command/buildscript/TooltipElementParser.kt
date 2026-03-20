package second_in_command.buildscript

import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import java.awt.Color

/**
 * Correlates recorded proxy calls with rendered element results to build TooltipElement lists.
 *
 * Previously relied on getChildrenCopy() to get positions, but Starsector's addPara (and other
 * calls beyond addTitle/addSpacer) does not add immediate direct children to the UIPanelAPI child
 * list at call-time. This caused getChildrenCopy() to return only 2 children (title + first spacer)
 * for every skill, silently dropping all subsequent elements via `?: continue`.
 *
 * Fixed approach: get position directly from the call's return value. Every method that creates
 * a UI element returns a LabelAPI / UIComponentAPI / UIPanelAPI, all of which expose .position.
 * Highlight/padding data still comes from the proxy recording.
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

    @Suppress("UNUSED_PARAMETER")
    fun parse(
        recordedCalls: List<RecordedCall>,
        realTooltip: TooltipMakerAPI
    ): List<TooltipElement> {
        // NOTE: We no longer call getChildrenCopy() here. Starsector's addPara and similar calls
        // do not always appear as immediate direct panel children, so matching by child index is
        // unreliable. Instead we extract position from the call's return value directly.
        val elements = mutableListOf<TooltipElement>()
        var currentFont: String? = null

        for (call in recordedCalls) {
            when {
                call.methodName == "addTitle" -> {
                    if (call.result == null) continue
                    elements.add(parseLabelFromCall(call, currentFont, isTitle = true))
                }

                call.methodName == "addPara" -> {
                    if (call.result == null) continue
                    elements.add(parseLabelFromCall(call, currentFont))
                }

                call.methodName == "addSpacer" -> {
                    val height = (call.args.getOrNull(0) as? Number)?.toFloat() ?: 0f
                    val position = extractPositionFromResult(call.result)
                    elements.add(SpacerElement(height = height, position = position))
                }

                call.methodName == "beginImageWithText" -> {
                    // Don't emit an element here — addImageWithText handles it.
                }

                call.methodName == "addImageWithText" -> {
                    elements.add(parseImageWithText(call))
                }

                call.methodName == "addSectionHeading" -> {
                    elements.add(parseSectionHeading(call))
                }

                call.methodName == "addImage" || call.methodName == "addImages" -> {
                    elements.add(parseImage(call))
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
                    // Unknown method — skip without advancing any index
                }
            }
        }

        return elements
    }

    private fun parseLabelFromCall(
        call: RecordedCall,
        font: String?,
        isTitle: Boolean = false
    ): LabelElement {
        val label = call.result as LabelAPI
        val text = label.text ?: ""
        val baseColor = label.color ?: Color.WHITE
        // Get position directly from the returned LabelAPI; it implements UIComponentAPI.getPosition()
        val position = extractPositionFromResult(label)
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

    private fun parseImageWithText(call: RecordedCall): ImageWithTextElement {
        val beginCall = call.pairedBeginCall
        val spriteName = beginCall?.args?.getOrNull(0) as? String ?: ""
        val imageHeight = (beginCall?.args?.getOrNull(1) as? Number)?.toFloat() ?: 48f
        val padding = extractPadding(call)

        // Parse inner elements from the inner recorder.
        // The inner recorder's call results have positions within the inner tooltip's space.
        val innerElements = if (beginCall?.innerRecorder != null && beginCall.innerRealTooltip != null) {
            parse(beginCall.innerRecorder!!.recordedCalls, beginCall.innerRealTooltip!!)
        } else {
            emptyList()
        }

        // Get position from the UIPanelAPI returned by addImageWithText
        val position = extractPositionFromResult(call.result)

        return ImageWithTextElement(
            spriteName = spriteName,
            assetFileName = spriteToAssetName(spriteName),
            imageHeight = imageHeight,
            padding = padding,
            children = innerElements,
            position = position
        )
    }

    private fun parseSectionHeading(call: RecordedCall): SectionHeadingElement {
        val args = call.args
        val text = args.getOrNull(0) as? String ?: ""
        var textColor = Color.WHITE
        var bgColor = Color.DARK_GRAY
        var alignment = "MID"
        var padding = 0f

        if (args.size >= 5) {
            textColor = args[1] as? Color ?: Color.WHITE
            bgColor = args[2] as? Color ?: Color.DARK_GRAY
            alignment = args[3]?.toString() ?: "MID"
            padding = (args[4] as? Number)?.toFloat() ?: 0f
        } else if (args.size >= 3) {
            alignment = args[1]?.toString() ?: "MID"
            padding = (args[2] as? Number)?.toFloat() ?: 0f
        }

        return SectionHeadingElement(
            text = text,
            textColor = ColorData.fromColor(textColor),
            bgColor = ColorData.fromColor(bgColor),
            alignment = alignment,
            padding = padding,
            position = extractPositionFromResult(call.result)
        )
    }

    private fun parseImage(call: RecordedCall): ImageElement {
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

        // Position (and actual rendered size) from the child captured right after addImage in the proxy
        val pos = extractPositionFromResult(call.result)
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
     * Extract a PositionData from any call result (LabelAPI, UIComponentAPI, UIPanelAPI).
     * All of these expose getPosition() → PositionAPI in the public Starsector API.
     * Returns a zero position on failure rather than throwing.
     */
    private fun extractPositionFromResult(result: Any?): PositionData {
        if (result == null) return PositionData(0f, 0f, 0f, 0f)
        return try {
            val pos = (result as UIComponentAPI).position
            PositionData(pos.x, pos.y, pos.width, pos.height)
        } catch (_: Exception) {
            PositionData(0f, 0f, 0f, 0f)
        }
    }

    /**
     * Build highlight ranges by combining proxy-recorded highlight info with the rendered text.
     */
    private fun buildHighlightRanges(call: RecordedCall, text: String): List<HighlightRange> {
        val ranges = mutableListOf<HighlightRange>()

        var highlightStrings = extractHighlightStringsFromArgs(call)
        var defaultHighlightColor = extractHighlightColorFromArgs(call)
        var perHighlightColors: Array<Color>? = extractPerHighlightColorsFromArgs(call)

        for (labelCall in call.labelCalls) {
            when (labelCall.methodName) {
                "setHighlight" -> {
                    highlightStrings = labelCall.args.filterIsInstance<String>().toList()
                    if (highlightStrings.isEmpty() && labelCall.args.isNotEmpty()) {
                        val first = labelCall.args[0]
                        if (first is Array<*>) {
                            highlightStrings = first.filterIsInstance<String>()
                        }
                    }
                }
                "setHighlightColor" -> {
                    val color = labelCall.args.getOrNull(0) as? Color
                    if (color != null) defaultHighlightColor = color
                }
                "setHighlightColors" -> {
                    val first = labelCall.args.getOrNull(0)
                    if (first is Array<*>) {
                        @Suppress("UNCHECKED_CAST")
                        perHighlightColors = first as? Array<Color>
                    }
                }
            }
        }

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

    private fun extractHighlightStringsFromArgs(call: RecordedCall): List<String> {
        if (call.methodName != "addPara") return emptyList()
        val args = call.args
        if (args.size <= 2) return emptyList()

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

    private fun extractHighlightColorFromArgs(call: RecordedCall): Color? {
        if (call.methodName != "addPara") return null
        val args = call.args
        if (args.size <= 2) return null

        val arg2 = args[2]
        if (arg2 is Array<*> && arg2.isNotEmpty() && arg2[0] is Color) {
            return arg2[0] as Color
        }

        val colors = mutableListOf<Color>()
        for (i in 2 until args.size) {
            if (args[i] is Color) colors.add(args[i] as Color) else break
        }

        return when (colors.size) {
            1 -> colors[0]
            2 -> colors[1]
            else -> null
        }
    }

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

    private fun extractPadding(call: RecordedCall): Float {
        return when (call.methodName) {
            "addPara" -> (call.args.getOrNull(1) as? Number)?.toFloat() ?: 0f
            "addSpacer" -> (call.args.getOrNull(0) as? Number)?.toFloat() ?: 0f
            "addImageWithText" -> (call.args.getOrNull(0) as? Number)?.toFloat() ?: 0f
            "addTitle" -> 0f
            else -> 0f
        }
    }

    /**
     * Convert a sprite path like "graphics/icons/abilities/sc_re_evaluate.png"
     * to a flat asset filename like "sc_re_evaluate.png".
     */
    fun spriteToAssetName(spritePath: String): String {
        if (spritePath.isEmpty()) return ""
        return spritePath.substringAfterLast("/").substringAfterLast("\\")
    }
}
