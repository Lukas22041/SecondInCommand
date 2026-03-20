package second_in_command.buildscript

import com.fs.starfarer.api.ui.*
import java.awt.Color

/**
 * Uses Kotlin interface delegation to wrap TooltipMakerAPI and record all method calls.
 * This captures data that has no public getter on rendered children (highlights, padding, sprite paths).
 * The real TooltipMakerAPI still executes — so children are created in the real panel for later parsing.
 *
 * Uses `by` delegation instead of java.lang.reflect.Proxy to avoid classloader restrictions.
 */
class RecordingTooltipMaker(private val real: TooltipMakerAPI) : TooltipMakerAPI by real {

    val recordedCalls = mutableListOf<RecordedCall>()

    private var pendingBeginImageWithText: RecordedCall? = null

    private fun record(methodName: String, args: List<Any?>, result: Any?): RecordedCall {
        val call = RecordedCall(methodName, args, result)
        recordedCalls.add(call)
        return call
    }

    private fun wrapLabel(label: LabelAPI, call: RecordedCall): LabelAPI {
        return RecordingLabel(label, call)
    }

    // --- addPara overloads ---

    override fun addPara(format: String, pad: Float, hl: Color, vararg highlights: String): LabelAPI {
        val result = real.addPara(format, pad, hl, *highlights)
        val call = record("addPara", listOf(format, pad, hl, highlights), result)
        return wrapLabel(result, call)
    }

    override fun addPara(str: String, pad: Float): LabelAPI {
        val result = real.addPara(str, pad)
        val call = record("addPara", listOf(str, pad), result)
        return wrapLabel(result, call)
    }

    override fun addPara(str: String, color: Color, pad: Float): LabelAPI {
        val result = real.addPara(str, color, pad)
        val call = record("addPara", listOf(str, color, pad), result)
        return wrapLabel(result, call)
    }

    override fun addPara(format: String, pad: Float, color: Color, hl: Color, vararg highlights: String): LabelAPI {
        val result = real.addPara(format, pad, color, hl, *highlights)
        val call = record("addPara", listOf(format, pad, color, hl, highlights), result)
        return wrapLabel(result, call)
    }

    override fun addPara(format: String, pad: Float, hl: Array<out Color>, vararg highlights: String): LabelAPI {
        val result = real.addPara(format, pad, hl, *highlights)
        val call = record("addPara", listOf(format, pad, hl, highlights), result)
        return wrapLabel(result, call)
    }

    // --- addTitle ---

    override fun addTitle(text: String): LabelAPI {
        val result = real.addTitle(text)
        val call = record("addTitle", listOf(text), result)
        return wrapLabel(result, call)
    }

    override fun addTitle(text: String, color: Color): LabelAPI {
        // The real TooltipMakerAPI has addTitle(String, Color) but the base addTitle(String) is defined in interface
        // We need to call through reflection or check if there's a 2-arg version
        val result = real.addTitle(text, color)
        val call = record("addTitle", listOf(text, color), result)
        return wrapLabel(result, call)
    }

    // --- addSpacer ---

    override fun addSpacer(height: Float): UIComponentAPI {
        val result = real.addSpacer(height)
        record("addSpacer", listOf(height), result)
        return result
    }

    // --- addSectionHeading ---

    override fun addSectionHeading(str: String, align: Alignment, pad: Float): LabelAPI {
        val result = real.addSectionHeading(str, align, pad)
        val call = record("addSectionHeading", listOf(str, align, pad), result)
        return wrapLabel(result, call)
    }

    override fun addSectionHeading(str: String, textColor: Color, bgColor: Color, align: Alignment, pad: Float): LabelAPI {
        val result = real.addSectionHeading(str, textColor, bgColor, align, pad)
        val call = record("addSectionHeading", listOf(str, textColor, bgColor, align, pad), result)
        return wrapLabel(result, call)
    }

    // --- beginImageWithText / addImageWithText ---

    override fun beginImageWithText(spriteName: String, imageHeight: Float): TooltipMakerAPI {
        val innerReal = real.beginImageWithText(spriteName, imageHeight)
        val innerRecorder = RecordingTooltipMaker(innerReal)
        val call = record("beginImageWithText", listOf(spriteName, imageHeight), innerReal)
        call.innerRecorder = innerRecorder
        call.innerRealTooltip = innerReal
        pendingBeginImageWithText = call
        return innerRecorder
    }

    override fun beginImageWithText(spriteName: String, imageHeight: Float, widthWithImage: Float, midAlignImage: Boolean): TooltipMakerAPI {
        val innerReal = real.beginImageWithText(spriteName, imageHeight, widthWithImage, midAlignImage)
        val innerRecorder = RecordingTooltipMaker(innerReal)
        val call = record("beginImageWithText", listOf(spriteName, imageHeight, widthWithImage, midAlignImage), innerReal)
        call.innerRecorder = innerRecorder
        call.innerRealTooltip = innerReal
        pendingBeginImageWithText = call
        return innerRecorder
    }

    override fun addImageWithText(pad: Float): UIPanelAPI {
        val result = real.addImageWithText(pad)
        val call = record("addImageWithText", listOf(pad), result)
        call.pairedBeginCall = pendingBeginImageWithText
        pendingBeginImageWithText = null
        return result
    }

    // --- addImage ---

    override fun addImage(spriteName: String, pad: Float) {
        real.addImage(spriteName, pad)
        record("addImage", listOf(spriteName, pad), null)
    }

    override fun addImage(spriteName: String, width: Float, pad: Float) {
        real.addImage(spriteName, width, pad)
        record("addImage", listOf(spriteName, width, pad), null)
    }

    override fun addImage(spriteName: String, width: Float, height: Float, pad: Float) {
        real.addImage(spriteName, width, height, pad)
        record("addImage", listOf(spriteName, width, height, pad), null)
    }

    override fun addImages(width: Float, height: Float, pad: Float, imagePad: Float, vararg spriteNames: String) {
        real.addImages(width, height, pad, imagePad, *spriteNames)
        record("addImages", listOf(width, height, pad, imagePad, spriteNames), null)
    }

    // --- Font methods (record for tracking, no child created) ---

    override fun setParaFont(paraFont: String) {
        real.setParaFont(paraFont)
        record("setParaFont", listOf(paraFont), null)
    }

    override fun setParaFontDefault() {
        real.setParaFontDefault()
        record("setParaFontDefault", emptyList(), null)
    }

    override fun setParaSmallInsignia() {
        real.setParaSmallInsignia()
        record("setParaSmallInsignia", emptyList(), null)
    }

    override fun setParaFontVictor14() {
        real.setParaFontVictor14()
        record("setParaFontVictor14", emptyList(), null)
    }

    override fun setParaSmallOrbitron() {
        real.setParaSmallOrbitron()
        record("setParaSmallOrbitron", emptyList(), null)
    }

    override fun setParaFontOrbitron() {
        real.setParaFontOrbitron()
        record("setParaFontOrbitron", emptyList(), null)
    }

    override fun setParaOrbitronLarge() {
        real.setParaOrbitronLarge()
        record("setParaOrbitronLarge", emptyList(), null)
    }

    override fun setParaOrbitronVeryLarge() {
        real.setParaOrbitronVeryLarge()
        record("setParaOrbitronVeryLarge", emptyList(), null)
    }

    override fun setParaInsigniaLarge() {
        real.setParaInsigniaLarge()
        record("setParaInsigniaLarge", emptyList(), null)
    }

    override fun setParaInsigniaVeryLarge() {
        real.setParaInsigniaVeryLarge()
        record("setParaInsigniaVeryLarge", emptyList(), null)
    }
}

/**
 * Wraps a LabelAPI to record setHighlight/setHighlightColors calls.
 * Also implements UIComponentAPI since actual labels implement both.
 */
class RecordingLabel(
    private val real: LabelAPI,
    private val parentRecord: RecordedCall
) : LabelAPI by real, UIComponentAPI by (real as UIComponentAPI) {

    // Resolve conflicts between LabelAPI and UIComponentAPI (shared methods)
    override fun getPosition(): PositionAPI = real.position
    override fun render(alphaMult: Float) = real.render(alphaMult)
    override fun advance(amount: Float) = real.advance(amount)
    override fun setOpacity(opacity: Float) = real.setOpacity(opacity)
    override fun getOpacity(): Float = real.opacity

    // --- Record highlight calls ---

    override fun setHighlight(vararg substrings: String) {
        real.setHighlight(*substrings)
        parentRecord.labelCalls.add(RecordedCall("setHighlight", listOf(substrings), null))
    }

    override fun setHighlightColor(color: Color) {
        real.setHighlightColor(color)
        parentRecord.labelCalls.add(RecordedCall("setHighlightColor", listOf(color), null))
    }

    override fun setHighlightColors(vararg colors: Color) {
        real.setHighlightColors(*colors)
        parentRecord.labelCalls.add(RecordedCall("setHighlightColors", listOf(colors), null))
    }
}

/**
 * A single recorded method call on TooltipMakerAPI or LabelAPI.
 */
class RecordedCall(
    val methodName: String,
    val args: List<Any?>,
    val result: Any?
) {
    /** Subsequent calls on the LabelAPI returned by this call (e.g. setHighlight, setHighlightColors) */
    val labelCalls = mutableListOf<RecordedCall>()

    /** For beginImageWithText: the recorder wrapping the inner TooltipMakerAPI */
    var innerRecorder: RecordingTooltipMaker? = null

    /** For beginImageWithText: the real (unwrapped) inner TooltipMakerAPI */
    var innerRealTooltip: TooltipMakerAPI? = null

    /** For addImageWithText: link back to the paired beginImageWithText call */
    var pairedBeginCall: RecordedCall? = null
}
