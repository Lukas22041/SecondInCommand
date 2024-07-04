package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import second_in_command.SCData
import second_in_command.specs.SCOfficer
import java.awt.Color

class SCOfficerPickerElement(var officer: SCOfficer?, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var noOfficerSprite = Global.getSettings().getSprite("graphics/secondInCommand/no_officer.png")
    var officerSprite: SpriteAPI? = null
    var isInEditMode = false

    var hoverFade = 0f


    init {
        enableTransparency = true
        borderAlpha = 0.5f
        borderColor = color
        renderBackground = false

        if (officer != null) {
            officerSprite = Global.getSettings().getSprite(officer!!.person.portraitSprite)
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        if (isHovering && !isInEditMode) {
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

        if (officer == null) {
            noOfficerSprite.setNormalBlend()
            noOfficerSprite.alphaMult = alphaMult * 0.7f
            noOfficerSprite.setSize(width, height)
            noOfficerSprite.render(x, y)

            noOfficerSprite.setAdditiveBlend()
            noOfficerSprite.alphaMult = alphaMult * 0.3f * hoverFade
            noOfficerSprite.setSize(width, height)
            noOfficerSprite.render(x, y)
        } else {

            var alpha = 1f
            if (isInEditMode) {
                alpha = 0.3f
            }

            officerSprite!!.setNormalBlend()
            officerSprite!!.alphaMult = alphaMult * 0.7f * alpha
            officerSprite!!.setSize(width, height)
            officerSprite!!.render(x, y)

            officerSprite!!.setAdditiveBlend()
            officerSprite!!.alphaMult = alphaMult * 0.3f * hoverFade
            officerSprite!!.setSize(width, height)
            officerSprite!!.render(x, y)
        }


    }
}