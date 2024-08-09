package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import second_in_command.misc.getAndLoadSprite
import java.awt.Color

class HelpIconElement(var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var sprite = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/helpIcon.png")

    init {
        renderBorder = false
        renderBackground = false

        onHoverEnter {
            playScrollSound()
        }
    }

    override fun render(alphaMult: Float) {

        var alpha = 0.8f
        if (isHovering) alpha = 1f
        sprite.alphaMult = alphaMult * alpha
        sprite.color = color
        sprite.setSize(width, height)
        sprite.render(x, y)

    }

}