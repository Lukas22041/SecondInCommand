package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import org.magiclib.kotlin.setAlpha
import java.awt.Color

class SkillGapElement(var color: Color, tooltip: TooltipMakerAPI, var heightOffset: Float = 72f) : LunaElement(tooltip, 34f, 1f) {

    var arrowSprite = Global.getSettings().getSprite("graphics/secondInCommand/arrow.png")
    var renderArrow = false

    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

        if (renderArrow) {
            arrowSprite.setNormalBlend()
            arrowSprite.alphaMult = alphaMult * 0.8f
            arrowSprite.color = color
            arrowSprite.renderAtCenter(x + width / 2 + 1, y - heightOffset / 2 + 1)
        }




    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        //super.processInput(events) Prevent Inputs from being consumed
    }
}