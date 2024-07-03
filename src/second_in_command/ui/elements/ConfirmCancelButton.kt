package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import java.awt.Color

class ConfirmCancelButton(var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var border = Global.getSettings().getSprite("graphics/secondInCommand/skillBorderInactive.png")

    var bounce = 0f
    var switchBounce = false
    var blink = true

    init {
        enableTransparency = true
        renderBorder = false
        backgroundAlpha = 0.2f


        innerElement.setParaFont("graphics/fonts/victor14.fnt")

        onHoverEnter {
            playScrollSound()
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        backgroundColor = color

        if (!switchBounce && blink) {
            bounce += 1.5f * amount
        } else if (blink){
            bounce -= 1.5f * amount
        }

        if (bounce >= 1f) {
            switchBounce = true
        }

        if (bounce <= 0f && switchBounce) {
            switchBounce = false
        }

        var alpha = 0.2f
        var extraAlpha = 0f
        extraAlpha += 0.1f * bounce

        if (isHovering) extraAlpha = 0.2f


        backgroundAlpha = alpha + extraAlpha

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

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
    }
}