package second_in_command.ui.elements

import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement

class SeedDisplayElement(var tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var init = false
    var remove = false

    override fun render(alphaMult: Float) {

        var alpha = alphaMult

        if (alphaMult >= 1) {
            init = true
        }

        if (init && alphaMult <= 0.99) {
            elementPanel.removeComponent(this.innerElement)
            remove = true
            alpha = 0f
        }

        super.render(alpha)
    }

    override fun renderBelow(alphaMult: Float) {
        var alpha = alphaMult
        if (remove) alpha = 0f
        super.renderBelow(alpha)
    }



}