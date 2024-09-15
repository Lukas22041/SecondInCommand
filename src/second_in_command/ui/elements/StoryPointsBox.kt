package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement

class StoryPointsBox(tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var points = Global.getSector().playerPerson.stats.storyPoints
    var textPara: LabelAPI
    var textColor = Misc.getBasePlayerColor()

    var numberPara: LabelAPI
    var numberColor = Misc.getStoryOptionColor()

    init {
        renderBorder = false
        renderBackground = false

        var inner = this.innerElement

        textPara = inner.addPara("Story Points", 0f, textColor, textColor)
        inner.setParaFont(Fonts.ORBITRON_24AABOLD)
        numberPara = inner.addPara("$points", 0f, numberColor, numberColor)


    }

    override fun advance(amount: Float) {
        super.advance(amount)

        points = Global.getSector().playerPerson.stats.storyPoints

        if (isHovering) {
            textPara.setColor(textColor.brighter())
            numberPara.setColor(numberColor.brighter())
        } else {
            textPara.setColor(textColor)
            numberPara.setColor(numberColor)
        }

        var textHeight = textPara.computeTextHeight(textPara.text)
        numberPara.text = "$points"

        textPara!!.position.inTL(width / 2 - textPara!!.computeTextWidth(textPara!!.text) / 2, 0f)
        numberPara!!.position.inTL(width / 2 - numberPara!!.computeTextWidth(numberPara!!.text) / 2, textHeight+5)
    }
}