package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.FaderUtil
import lunalib.lunaUI.elements.LunaElement
import second_in_command.misc.getAndLoadSprite
import second_in_command.misc.levelBetween
import second_in_command.skills.scavenging.scripts.ScrapManager
import java.awt.Color

class ScrapBar(var scrapManager: ScrapManager, tooltip: TooltipMakerAPI, width: Float, height: Float, var scrapGain: Int? = 0) : LunaElement(tooltip, width, height) {

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

    var scrapPara: LabelAPI
    var scrapGainPara: LabelAPI

    var gainFader = FaderUtil(1f, 0.75f, 0.75f, false, false)
    var consumeFader = FaderUtil(1f, 0.5f, 0.5f, false, false)


    init {
        enableTransparency = true
        renderBorder = false
        renderBackground = false

        scrapPara = innerElement.addPara("", 0f, textColor, textColor)
        scrapGainPara = innerElement.addPara("", 0f, textColor, textColor.brighter())

        updateLabel()

    }

    fun updateLabel() {
        scrapPara.text = "${scrapManager.getCurrentScrap()}% / ${scrapManager.getMaxScrap()}%"
        scrapPara.position.inTL(width/2-scrapPara.computeTextWidth(scrapPara.text)/2, -scrapPara.computeTextHeight(scrapPara.text))


        if (scrapGain != 0) {
            scrapGainPara.text = "Scavenged $scrapGain% Scrap"
            scrapGainPara.position.inTL(width/2-scrapGainPara.computeTextWidth(scrapGainPara.text)/2, scrapGainPara.computeTextHeight(scrapGainPara.text)+3f)
            scrapGainPara.setHighlight("$scrapGain%")
        }

    }

    override fun advance(amount: Float) {
        super.advance(amount)
        updateLabel()

        gainFader.advance(amount)
        if (gainFader.brightness >= 1)
        {
            gainFader.fadeOut()
        }
        else if (gainFader.brightness <= 0)
        {
            gainFader.fadeIn()
        }



        consumeFader.advance(amount)
        if (consumeFader.brightness >= 1)
        {
            consumeFader.fadeOut()
        }
        else if (consumeFader.brightness <= 0.1)
        {
            consumeFader.fadeIn()
        }

        if (scrapManager.getScrapAboutToBeConsumed() == 0) {
            consumeFader.brightness = 1f
            consumeFader.fadeOut()
        }
    }

    fun easeInOutSine(x: Float): Float {
        return (-(Math.cos(Math.PI * x) - 1) / 2).toFloat();
    }

    fun getLevel() : Float {
        var current = scrapManager.getCurrentScrap().toFloat()
        var level = current.levelBetween(0f, scrapManager.getMaxScrap().toFloat())
        return level
    }

    fun getGainLevel() : Float {
        var level = scrapGain!!.toFloat().levelBetween(0f, scrapManager.getMaxScrap().toFloat())
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
        active_fill.setNormalBlend()
        active_fill.color = innerColor
        active_fill.alphaMult = alphaMult
        active_fill.render(x+4, y+1)

        if (scrapGain != 0) {
            var gainLevel = getGainLevel()
            var gainFillWidth = Math.round(ogFillWidth*gainLevel).toFloat()

            active_fill.setSize(gainFillWidth, ogFillHeight)
            active_fill.setAdditiveBlend()
            active_fill.color = innerColor.brighter()
            active_fill.alphaMult = alphaMult * 0.5f * easeInOutSine(gainFader.brightness)
            active_fill.render(x+4+fillWidth-gainFillWidth, y+1)
        }

        var toBeConsumed = scrapManager.getScrapAboutToBeConsumed()
        if (toBeConsumed != 0) {
            var consumptionLevel = toBeConsumed.toFloat().levelBetween(0f, scrapManager.getCurrentScrap().toFloat())
            var consumptionFillWidth = fillWidth*consumptionLevel
            var barX = x+4+fillWidth-consumptionFillWidth

            active_fill.setSize(consumptionFillWidth, ogFillHeight)
            active_fill.setNormalBlend()
            active_fill.color = innerColor.darker().darker().darker()
            active_fill.alphaMult = alphaMult * 0.5f * easeInOutSine(consumeFader.brightness)
            active_fill.render(barX, y+1)
        }

        divider.color = innerColor.brighter()
        divider.alphaMult = alphaMult
        divider.render(x+fillWidth+4f, y)
    }
}