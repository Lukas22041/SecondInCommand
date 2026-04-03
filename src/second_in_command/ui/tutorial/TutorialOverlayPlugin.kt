package second_in_command.ui.tutorial

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import java.awt.Color
import org.dark.shaders.util.ShaderLib
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import second_in_command.SCUtils
import second_in_command.ui.elements.ConfirmCancelButton
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Full-screen tutorial overlay rendered on top of the SiC skill panel.
 *
 * Lifecycle (caller is responsible):
 *  1. `val plugin  = TutorialOverlayPlugin(parent, steps, panelW, panelH)`
 *  2. `val overlay = Global.getSettings().createCustom(panelW, panelH, plugin)`
 *  3. `plugin.panel = overlay`
 *  4. `parent.addComponent(overlay) ; overlay.position.inTL(0f, 0f)`
 *  5. `plugin.rebuildTextBox()`
 *
 * @param parentPanel  The UIPanelAPI that owns the overlay (used for removal).
 * @param steps        Ordered list of [TutorialStep]s to present.
 * @param panelWidth   Logical width of the overlay panel (= parent width).
 * @param panelHeight  Logical height of the overlay panel (= parent height).
 */
class TutorialOverlayPlugin(
    private val parentPanel: UIPanelAPI,
    private val steps: List<TutorialStep>,
    private val panelWidth: Float,
    private val panelHeight: Float
) : CustomUIPanelPlugin {

    /** Set by the caller immediately after `createCustom`. */
    var panel: UIPanelAPI? = null

    private var position: PositionAPI? = null
    private var currentStep = 0
    private var pulseTime = 0f

    // Text-box sub-panel managed by rebuildTextBox()
    private var textBoxPanel: CustomPanelAPI? = null

    // Cached UI-space origin of the text box (set in rebuildTextBox, used in render* calls)
    private var tbUiX = 0f
    private var tbUiY = 0f

    // Per-step dimensions — synced from the current TutorialStep at the top of rebuildTextBox()
    private var tbWidth  = 330f
    private var tbHeight = 240f

    // ── Shader (shared across all overlay instances in a session) ─────────────

    companion object {
        private var spotlightShader = 0
        private var shaderAttempted = false
    }

    private fun ensureShader() {
        if (shaderAttempted) return
        shaderAttempted = true
        try {
            spotlightShader = ShaderLib.loadShader(
                Global.getSettings().loadText("data/shaders/baseVertex.shader"),
                Global.getSettings().loadText("data/shaders/tutorialSpotlightFragment.shader")
            )
        } catch (_: Exception) {
            spotlightShader = 0
        }
    }

    // ── CustomUIPanelPlugin ───────────────────────────────────────────────────

    override fun positionChanged(position: PositionAPI?) {
        this.position = position
    }

    /**
     * Called BEFORE child panels are rendered.
     * Draws:
     *  - the spotlight darkening effect (shader or 4-rect fallback)
     *  - the text-box background and border
     */
    override fun renderBelow(alphaMult: Float) {
        val pos  = position ?: return
        val step = steps.getOrNull(currentStep) ?: return
        ensureShader()

        val screenW = Global.getSettings().screenWidth.toFloat()
        val screenH = Global.getSettings().screenHeight.toFloat()
        // UI scale factor: the shader's focusRect / fadeRadius uniforms compare against
        // gl_FragCoord (always in physical pixels), so GL drawing coords must be
        // multiplied by uiScale to convert to pixel space for those uniforms.
        val uiScale = Global.getSettings().screenScaleMult

        // Convert focus rect from UI space (origin top-left) to GL space (origin bottom-left).
        // When darkenFull there is no spotlight — the whole screen is uniformly darkened.
        val glFX: Float
        val glFY: Float
        val glFW: Float
        val glFH: Float
        if (step.darkenFull) {
            glFX = 0f; glFY = 0f; glFW = 0f; glFH = 0f   // unused placeholders
        } else {
            glFX = pos.x + step.focusX
            glFY = pos.y + pos.height - step.focusY - step.focusHeight
            glFW = step.focusWidth
            glFH = step.focusHeight
        }

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        if (step.darkenFull) {
            // No spotlight — uniform full-screen darkening, text-box rendered below as usual
            GL11.glColor4f(0f, 0f, 0f, 0.78f * alphaMult)
            GL11.glRectf(0f, 0f, screenW, screenH)
        } else if (spotlightShader != 0) {
            // ── Smooth vignette cutout via shader ─────────────────────────────
            // gl_FragCoord is always in actual screen pixels, so focusRect and fadeRadius
            // must be scaled from GL drawing units to pixels via uiScale.
            GL20.glUseProgram(spotlightShader)
            GL20.glUniform2f(GL20.glGetUniformLocation(spotlightShader, "screenSize"), screenW * uiScale, screenH * uiScale)
            GL20.glUniform4f(GL20.glGetUniformLocation(spotlightShader, "focusRect"),
                glFX * uiScale, glFY * uiScale, glFW * uiScale, glFH * uiScale)
            GL20.glUniform1f(GL20.glGetUniformLocation(spotlightShader, "fadeRadius"),  35f * uiScale)
            GL20.glUniform1f(GL20.glGetUniformLocation(spotlightShader, "darkenAlpha"), 0.78f * alphaMult)

            // Full-screen quad — drawn large enough to cover the entire screen;
            // the viewport clips any excess.
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glVertex2f(0f,      0f)
            GL11.glVertex2f(screenW, 0f)
            GL11.glVertex2f(screenW, screenH)
            GL11.glVertex2f(0f,      screenH)
            GL11.glEnd()

            GL20.glUseProgram(0)
        } else {
            // ── Fallback: 4 opaque rectangles surrounding the focus area ──────
            GL11.glColor4f(0f, 0f, 0f, 0.78f * alphaMult)
            GL11.glRectf(0f,        0f,          screenW, glFY)             // below
            GL11.glRectf(0f,        glFY + glFH, screenW, screenH)         // above
            GL11.glRectf(0f,        glFY,         glFX,    glFY + glFH)     // left
            GL11.glRectf(glFX+glFW, glFY,         screenW, glFY + glFH)     // right
        }

        // ── Pulsing border around focus rect ─────────────────────────────────
        // Drawn BEFORE the text-box fill so the fill occludes it where they overlap.
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        if (!step.darkenFull) {
            val pulse     = sin(pulseTime * 2.5).toFloat() * 0.25f + 0.75f
            val baseColor = Misc.getBasePlayerColor()
            val r = baseColor.red   / 255f
            val g = baseColor.green / 255f
            val b = baseColor.blue  / 255f
            GL11.glLineWidth(2f)
            GL11.glColor4f(r, g, b, pulse * alphaMult)
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glVertex2f(glFX,        glFY)
            GL11.glVertex2f(glFX + glFW, glFY)
            GL11.glVertex2f(glFX + glFW, glFY + glFH)
            GL11.glVertex2f(glFX,        glFY + glFH)
            GL11.glVertex2f(glFX,        glFY)
            GL11.glEnd()
        }

        // ── Text-box: dark fill + player-colour border (drawn AFTER focus border) ──
        val tbGlX = pos.x + tbUiX
        val tbGlY = pos.y + pos.height - tbUiY - tbHeight
        val border = Misc.getDarkPlayerColor()

        GL11.glColor4f(0f, 0f, 0f, 0.92f * alphaMult)
        GL11.glRectf(tbGlX, tbGlY, tbGlX + tbWidth, tbGlY + tbHeight)

        GL11.glLineWidth(2f)
        GL11.glColor4f(border.red / 255f, border.green / 255f, border.blue / 255f, alphaMult)
        GL11.glBegin(GL11.GL_LINE_STRIP)
        GL11.glVertex2f(tbGlX,           tbGlY)
        GL11.glVertex2f(tbGlX + tbWidth, tbGlY)
        GL11.glVertex2f(tbGlX + tbWidth, tbGlY + tbHeight)
        GL11.glVertex2f(tbGlX,           tbGlY + tbHeight)
        GL11.glVertex2f(tbGlX,           tbGlY)
        GL11.glEnd()

        GL11.glPopMatrix()
    }

    /**
     * Called AFTER child panels are rendered.
     * Draws:
     *  - arrow from text-box edge to focus-rect center
     */
    override fun render(alphaMult: Float) {
        val pos  = position ?: return
        val step = steps.getOrNull(currentStep) ?: return

        val glFX = pos.x + step.focusX
        val glFY = pos.y + pos.height - step.focusY - step.focusHeight
        val glFW = step.focusWidth
        val glFH = step.focusHeight

        val tbGlX = pos.x + tbUiX
        val tbGlY = pos.y + pos.height - tbUiY - tbHeight

        val baseColor = Misc.getBasePlayerColor()
        val r = baseColor.red   / 255f
        val g = baseColor.green / 255f
        val b = baseColor.blue  / 255f

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)


        // ── Arrow: freeform exit-point direction ──────────────────────────────
        // Compute where the line from TB-centre to focus-centre exits the TB rect,
        // then draw from that exit point toward the focus rect.
        // Naturally suppressed when the boxes overlap (travel < 12 px).
        val tbCX    = tbGlX + tbWidth  / 2f
        val tbCY    = tbGlY + tbHeight / 2f
        val focusCX = glFX  + glFW / 2f
        val focusCY = glFY  + glFH / 2f

        val dx  = focusCX - tbCX
        val dy  = focusCY - tbCY
        val len = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        if (len > 0.5f) {
            val nx = dx / len
            val ny = dy / len

            // t at which the ray from TB-centre exits each edge of the TB rect
            var tExit = Float.MAX_VALUE
            if (nx > 0f)      tExit = minOf(tExit, (tbGlX + tbWidth  - tbCX) / nx)
            else if (nx < 0f) tExit = minOf(tExit, (tbGlX            - tbCX) / nx)
            if (ny > 0f)      tExit = minOf(tExit, (tbGlY + tbHeight - tbCY) / ny)
            else if (ny < 0f) tExit = minOf(tExit, (tbGlY            - tbCY) / ny)

            // Distance from TB edge to focus-rect centre, capped so the tip
            // doesn't overshoot the focus rect or travel unreasonably far.
            val travel = (len - tExit).coerceIn(0f, 90f)

            if (travel >= 12f) {
                val startX = tbCX + nx * tExit
                val startY = tbCY + ny * tExit
                val tipX   = startX + nx * travel
                val tipY   = startY + ny * travel

                val arrowHalf = 8f
                val perpX = -ny * arrowHalf
                val perpY =  nx * arrowHalf

                GL11.glLineWidth(1.5f)
                GL11.glColor4f(r, g, b, 0.7f * alphaMult)
                GL11.glBegin(GL11.GL_LINES)
                GL11.glVertex2f(startX, startY)
                GL11.glVertex2f(tipX - nx * 14f, tipY - ny * 14f)
                GL11.glEnd()

                GL11.glColor4f(r, g, b, 0.9f * alphaMult)
                GL11.glBegin(GL11.GL_TRIANGLES)
                GL11.glVertex2f(tipX, tipY)
                GL11.glVertex2f(tipX - nx * 14f + perpX, tipY - ny * 14f + perpY)
                GL11.glVertex2f(tipX - nx * 14f - perpX, tipY - ny * 14f - perpY)
                GL11.glEnd()
            }
        }

        GL11.glPopMatrix()
    }

    override fun advance(amount: Float) {
        pulseTime += amount
    }

    /**
     * Blocks all keyboard and mouse input from reaching the skill panel below.
     * ESC immediately skips the tutorial.
     * LunaElement buttons detect clicks via their own advance() hover-polling,
     * independent of this event list, so they still work correctly.
     */
    override fun processInput(events: MutableList<InputEventAPI>?) {
        if (events == null) return
        for (event in events) {
            if (event.isConsumed) continue
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE) {
                event.consume()
                skipTutorial()
                return
            }
            if (event.isKeyboardEvent || event.isMouseEvent) event.consume()
        }
    }

    override fun buttonPressed(buttonId: Any?) {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Set by the caller. Called once after each onLeave/onEnter pair so the
     * skill panel can be recreated to reflect any demo-state changes.
     * The implementation should also re-raise the overlay on top of the parent.
     */
    var onRefreshPanel: (() -> Unit)? = null

    /** Invoke the current step's onLeave callback (does NOT advance the index). */
    private fun leaveCurrentStep() {
        steps.getOrNull(currentStep)?.onLeave?.invoke()
    }

    /**
     * (Re)builds the text-box sub-panel for the current step.
     * Height is determined dynamically via a two-pass measurement so that the
     * GL border and panel always exactly wrap the content + buttons.
     * Must be called after [panel] is set and the panel has been added to parent.
     */
    fun rebuildTextBox() {
        val p    = panel ?: return
        val step = steps.getOrNull(currentStep) ?: return

        textBoxPanel?.let { p.removeComponent(it) }
        textBoxPanel = null

        tbWidth = step.textBoxWidth
        val isFirst = currentStep == 0
        val isLast  = currentStep >= steps.size - 1

        // ── Pass 1: measure ───────────────────────────────────────────────
        // Build into a throwaway panel so we can read getHeightSoFar() before
        // committing to a position (calcTextBoxPos needs tbHeight).
        val measurePanel = Global.getSettings().createCustom(tbWidth, 2000f, null)
        val measureEl    = measurePanel.createUIElement(tbWidth, 2000f, false)
        measurePanel.addUIElement(measureEl)
        measureEl.position.inTL(0f, 0f)
        buildContent(measureEl, step, isFirst, isLast)
        tbHeight = measureEl.getHeightSoFar() + 10f   // 10 px guaranteed bottom gap

        // ── Pass 2: real panel at the correct size ────────────────────────
        val (uiX, uiY) = calcTextBoxPos(step)
        tbUiX = uiX
        tbUiY = uiY

        val tbPanel = Global.getSettings().createCustom(tbWidth, tbHeight, null)
        p.addComponent(tbPanel)
        tbPanel.position.inTL(uiX, uiY)
        textBoxPanel = tbPanel

        val tbEl = tbPanel.createUIElement(tbWidth, tbHeight, false)
        tbPanel.addUIElement(tbEl)
        tbEl.position.inTL(0f, 0f)
        buildContent(tbEl, step, isFirst, isLast)

        // Notify the step it is now active; refresh the skill panel if state was changed
        step.onEnter?.invoke()
        onRefreshPanel?.invoke()
    }

    /**
     * Populates [tbEl] with the current step's content followed by the
     * Back / Next navigation buttons.  Called twice per [rebuildTextBox]:
     * once into a measurement panel and once into the real panel.
     */
    private fun buildContent(
        tbEl: TooltipMakerAPI,
        step: TutorialStep,
        isFirst: Boolean,
        isLast: Boolean
    ) {
        step.content(tbEl)

        tbEl.addSpacer(8f)

        val me    = this
        val btnH  = 28f
        val btnW  = 100f
        val btnGap = 8f
        val rowH  = btnH + 8f
        val lx    = (tbWidth - btnW * 2f - btnGap) / 2f

        val btnRow = tbEl.addLunaElement(tbWidth, rowH)
        btnRow.enableTransparency = true
        btnRow.backgroundAlpha = 0f
        btnRow.renderBorder = false

        val backColor = if (isFirst) Color(50, 50, 50) else Misc.getGrayColor()
        val backBtn   = ConfirmCancelButton(backColor, btnRow.innerElement, btnW, btnH)
        backBtn.blink = false
        backBtn.addText("< Back")
        backBtn.centerText()
        backBtn.elementPanel.position.inTL(lx, (rowH - btnH) / 2f)
        if (!isFirst) backBtn.onClick { backBtn.playClickSound(); me.previousStep() }

        val nextBtn = ConfirmCancelButton(Misc.getBasePlayerColor(), btnRow.innerElement, btnW, btnH)
        nextBtn.blink = false
        nextBtn.addText(if (isLast) "Finish" else "Next >")
        nextBtn.centerText()
        nextBtn.elementPanel.position.rightOfTop(backBtn.elementPanel, btnGap)
        nextBtn.onClick { nextBtn.playClickSound(); me.advanceStep() }
    }

    fun advanceStep() {
        leaveCurrentStep()
        currentStep++
        if (currentStep >= steps.size) {
            onRefreshPanel?.invoke()
            parentPanel.removeComponent(panel)
        } else {
            rebuildTextBox()
        }
    }

    fun previousStep() {
        if (currentStep > 0) {
            leaveCurrentStep()
            currentStep--
            rebuildTextBox()
        }
    }

    fun skipTutorial() = completeTutorial()

    fun completeTutorial() {
        leaveCurrentStep()
        onRefreshPanel?.invoke()
        parentPanel.removeComponent(panel)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun calcTextBoxPos(step: TutorialStep): Pair<Float, Float> {
        val margin = 12f

        // Explicit position takes priority — clamp to keep inside panel bounds
        if (step.textBoxX != null && step.textBoxY != null) {
            return Pair(
                step.textBoxX.coerceIn(margin, panelWidth  - tbWidth  - margin),
                step.textBoxY.coerceIn(margin, panelHeight - tbHeight - margin)
            )
        }

        // Auto-place relative to the focus rect, with generous spacing
        val gap = 30f
        var x: Float
        var y: Float

        when (step.textBoxSide) {
            TutorialStep.TextBoxSide.RIGHT -> {
                x = step.focusX + step.focusWidth + gap
                y = step.focusY + (step.focusHeight - tbHeight) / 2f
            }
            TutorialStep.TextBoxSide.LEFT -> {
                x = step.focusX - tbWidth - gap
                y = step.focusY + (step.focusHeight - tbHeight) / 2f
            }
            TutorialStep.TextBoxSide.TOP -> {
                x = step.focusX + (step.focusWidth - tbWidth) / 2f
                y = step.focusY - tbHeight - gap
            }
            TutorialStep.TextBoxSide.BOTTOM -> {
                x = step.focusX + (step.focusWidth - tbWidth) / 2f
                y = step.focusY + step.focusHeight + gap
            }
            TutorialStep.TextBoxSide.CENTER -> {
                x = (panelWidth  - tbWidth)  / 2f
                y = (panelHeight - tbHeight) / 2f
            }
        }

        x = x.coerceIn(margin, panelWidth  - tbWidth  - margin)
        y = y.coerceIn(margin, panelHeight - tbHeight - margin)

        return Pair(x, y)
    }
}

