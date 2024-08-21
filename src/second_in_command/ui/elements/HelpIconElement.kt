package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils
import second_in_command.misc.getAndLoadSprite
import java.awt.Color

class HelpIconElement(var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var sprite = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/helpIcon.png")

    var bounce = 0f
    var switchBounce = false

    init {
        renderBorder = false
        renderBackground = false

        onHoverEnter {
            playScrollSound()
            SCUtils.getSectorData().hoveredOverOfficerPickerHelp = true
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)


        var shouldBlink = !SCUtils.getSectorData().hoveredOverOfficerPickerHelp

        if (!switchBounce && shouldBlink) {
            bounce += 1.5f * amount
        } else if (shouldBlink){
            bounce -= 1.5f * amount
        } else {
            bounce = 0f
        }


        if (bounce >= 1f) {
            switchBounce = true
        }

        if (bounce <= 0f && switchBounce) {
            switchBounce = false
        }

        bounce = MathUtils.clamp(bounce, 0f, 1f)

    }

    override fun render(alphaMult: Float) {

        var alpha = 0.8f
        if (isHovering) alpha = 1f
        sprite.setNormalBlend()
        sprite.alphaMult = alphaMult * alpha
        sprite.color = color
        sprite.setSize(width, height)
        sprite.render(x, y)

        sprite.setAdditiveBlend()
        sprite.alphaMult = alphaMult * bounce * 0.6f
        sprite.color = color
        sprite.setSize(width, height)
        sprite.render(x, y)

    }

}