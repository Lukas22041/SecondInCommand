package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import org.magiclib.kotlin.setAlpha
import java.awt.Color

class SkillUnderlineElement(var color: Color, var lineHeight: Float, tooltip: TooltipMakerAPI, width: Float) : LunaElement(tooltip, width, lineHeight) {

    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        var curPos = x
        var segments = 30
        var alphaPerSegment = 255f * 2 / segments
        var curAlpha = 0f
        var curColor = color

        var swapped = false

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {
            curColor = curColor.setAlpha(curAlpha.coerceIn(0f, 255f).toInt())

            GL11.glColor4f(curColor.red / 255f,
                curColor.green / 255f,
                curColor.blue / 255f,
                curColor.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(curPos, y )
            GL11.glVertex2f(curPos, y+height)
            curPos += width / segments

            if (!swapped) {
                curAlpha += alphaPerSegment
            }
            else {
                curAlpha -= alphaPerSegment
            }

            if (curAlpha > 255) {
                swapped = true
            }
        }


        // GL11.glRectf(x, y , x + width * level, y + height)

        GL11.glEnd()
        GL11.glPopMatrix()



    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        //super.processInput(events) Prevent Inputs from being consumed
    }
}