package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import second_in_command.misc.getAndLoadSprite
import second_in_command.misc.levelBetween
import second_in_command.skills.scavenging.scripts.ScrapManager
import java.awt.Color

class ScrapBar(var scrapManager: ScrapManager, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var background = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/scavenging/ui/scrap_bar_border.png")
    var background_fill = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/scavenging/ui/scrap_bar_background_fill.png")
    var active_fill = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/scavenging/ui/scrap_bar_active_fill.png")
    var divider = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/scavenging/ui/divider.png")

    var ogFillWidth = active_fill.width
    var ogFillHeight = active_fill.height

    var bounce = 0f
    var switchBounce = false
    var blink = true

    var innerColor = Color(77,142,80,255).brighter()
    var textColor = Color(95,160,90,255)
    var label: LabelAPI

    init {
        enableTransparency = true
        renderBorder = false
        renderBackground = false

        label = innerElement.addPara("", 0f, textColor, textColor)
        updateLabel()
    }

    fun updateLabel() {
        label.text = "${scrapManager.getCurrentScrap()}% / ${scrapManager.getMaxScrap()}%"
        label.position.inTL(width/2-label.computeTextWidth(label.text)/2, -label.computeTextHeight(label.text))
    }

    override fun advance(amount: Float) {
        super.advance(amount)
        updateLabel()
    }

    fun getLevel() : Float {
        var current = scrapManager.getCurrentScrap().toFloat()
        var level = current.levelBetween(0f, scrapManager.getMaxScrap().toFloat())
        return level
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)



        var level = getLevel()

        background.color = innerColor;
        background.alphaMult = alphaMult
        background.render(x,y)

        background_fill.color = innerColor;
        background_fill.alphaMult = alphaMult
        background_fill.render(x,y)

        var fillWidth = Math.round(ogFillWidth*level).toFloat()

        active_fill.setSize(fillWidth, ogFillHeight)
        active_fill.color = innerColor
        active_fill.alphaMult = alphaMult
        active_fill.render(x+4, y+1)

        divider.color = innerColor.brighter()
        divider.alphaMult = alphaMult
        divider.render(x+fillWidth+4f, y)
    }
}