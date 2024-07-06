package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import second_in_command.misc.levelBetween
import java.awt.Color

class OfficerXPBar(var currentXP: Float, var requiredXP: Float, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var border = Global.getSettings().getSprite("graphics/secondInCommand/skillBorderInactive.png")


    init {
        enableTransparency = true
        renderBorder = false
        backgroundAlpha = 0.2f

        backgroundColor = color

       // innerElement.setParaFont("graphics/fonts/victor14.fnt")

        onHoverEnter {
            playScrollSound()
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

       /* border.setNormalBlend()
        border.color = color
        border.setSize(width, height)
        border.alphaMult = alphaMult
        border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        border.setAdditiveBlend()
        border.color = color
        border.setSize(width, height)
        border.alphaMult = alphaMult * 0.2f
        border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())*/
    }

    override fun renderBelow(alphaMult: Float) {

        var color = Color(0, 0, 0)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_CULL_FACE)

        GL11.glDisable(GL11.GL_BLEND)

       /* GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_FRONT)
        GL11.glFrontFace(GL11.GL_CW)*/
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * (alphaMult * 1f))




        GL11.glRectf(x, y , x + width , y + height)


        GL11.glPopMatrix()


        super.renderBelow(alphaMult)


        color = backgroundColor

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_CULL_FACE)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        /* GL11.glEnable(GL11.GL_CULL_FACE)
         GL11.glCullFace(GL11.GL_FRONT)
         GL11.glFrontFace(GL11.GL_CW)*/

        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(color.red / 255f,
            color.green / 255f,
            color.blue / 255f,
            color.alpha / 255f * (alphaMult * 0.2f))

        var level = currentXP.levelBetween(0f, requiredXP)

        GL11.glRectf(x, y , x + width * level, y + height)

        GL11.glPopMatrix()
    }
}