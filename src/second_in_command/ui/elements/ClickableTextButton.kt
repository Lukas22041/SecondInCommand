package second_in_command.ui.elements

import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import java.awt.Color

class ClickableTextButton(var text: String, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var label: LabelAPI
    var active = false

    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false

        //innerElement.setParaFont(Fonts.ORBITRON_12)
        label = innerElement.addPara(text, 0f, color, color)
        label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, height / 2 - label.computeTextHeight(label.text) / 2)

        onHoverEnter {
            playSound("ui_button_mouseover")
        }


    }

    /*override fun positionChanged(position: PositionAPI?) {
        label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, 0f)
    }*/

    override fun advance(amount: Float) {
        if (active) {
            label.color = color
        }
        else {
            label.color = color.darker()
        }

        if (isHovering) {
            label.color = label.color.brighter();
        }
    }
}