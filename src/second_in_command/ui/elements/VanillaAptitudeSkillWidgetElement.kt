package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import java.awt.Color

class VanillaAptitudeSkillWidgetElement(var id: String, var iconPath: String, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var sprite = Global.getSettings().getSprite(iconPath)
    var border = Global.getSettings().getSprite("graphics/secondInCommand/skillBorderVanillaAptitude.png")

    var hoverFade = 0f
    var time = 0f

    //var border = Global.getSettings().getSprite("test")

    init {
        enableTransparency = true
        backgroundAlpha = 0f
        borderAlpha = 0f

        onHoverEnter {
            //playSound("ui_button_mouseover")
            playScrollSound()
        }

        onClick {
            playClickSound()
        }

    }

    override fun advance(amount: Float) {
        super.advance(amount)

        time += 1 * amount

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0f, 1f)

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

       /* var mult = 1f
        if (!activated) mult = 0.5f*/


        sprite.setNormalBlend()
        sprite.setSize(width-4, height-4)
        sprite.alphaMult = alphaMult

        sprite.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        border.setNormalBlend()
        border.color = color
        border.setSize(width, height)
        border.alphaMult = alphaMult
        border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        border.setAdditiveBlend()
        border.color = color
        border.setSize(width, height)
        border.alphaMult = alphaMult * 0.2f
        border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        sprite.setAdditiveBlend()
        sprite.setSize(width-4, height-4)
        sprite.alphaMult = alphaMult * 0.5f * hoverFade
        sprite.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        var backgroundColor = Color(0, 0, 0)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_CULL_FACE)


        if (alphaMult <= 0.8f) {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            GL11.glDisable(GL11.GL_BLEND)
        }

        GL11.glColor4f(backgroundColor.red / 255f,
            backgroundColor.green / 255f,
            backgroundColor.blue / 255f,
            backgroundColor.alpha / 255f * (alphaMult * backgroundAlpha))

        GL11.glRectf(x, y , x + width, y + height)

        GL11.glPopMatrix()
    }
}