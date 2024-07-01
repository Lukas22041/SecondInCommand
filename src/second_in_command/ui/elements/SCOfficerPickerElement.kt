package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement

class SCOfficerPickerElement(tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var noOfficerSprite = Global.getSettings().getSprite("graphics/secondInCommand/no_officer.png")

    var hoverFade = 0f


    init {
        enableTransparency = true
        borderAlpha = 0.5f
        renderBackground = false
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0f, 1f)
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)




    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        noOfficerSprite.setNormalBlend()
        noOfficerSprite.alphaMult = alphaMult * 0.7f
        noOfficerSprite.setSize(width, height)
        noOfficerSprite.render(x, y)

        noOfficerSprite.setAdditiveBlend()
        noOfficerSprite.alphaMult = alphaMult * 0.3f * hoverFade
        noOfficerSprite.setSize(width, height)
        noOfficerSprite.render(x, y)
    }
}