package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import java.awt.Color

class FullDialogAptitudeBackgroundElement(var color: Color, tooltip: TooltipMakerAPI, var yOffset: Float) : LunaElement(tooltip, 0f, 0f) {

    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false
    }


    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_CULL_FACE)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        var size = 34f
        var length = 480f



        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * (alphaMult * backgroundAlpha * 0.125f))

        GL11.glRectf(x, y - size + yOffset, x + length, y + size + yOffset)

        GL11.glPopMatrix()

    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        //super.processInput(events) Prevent Inputs from being consumed
    }
}