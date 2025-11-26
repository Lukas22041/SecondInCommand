package second_in_command.ui.elements

import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCUtils
import java.awt.Color

class ScrapWidget(tooltip: TooltipMakerAPI, scrapGain: Float? = 0f) : LunaElement(tooltip,  24f+160f+4f, 16f) {

    init {

        enableTransparency = true
        renderBorder = false
        renderBackground = false

        var scrapIconPath = "graphics/secondInCommand/scavenging/ui/scrap.png"
        var icon = innerElement.addLunaSpriteElement(scrapIconPath, LunaSpriteElement.ScalingTypes.STRETCH_ELEMENT, 24f, 16f)
        icon.getSprite().color = Color(77,142,80,255).brighter()
        icon.position.inTL(0f, 0f)

        var manager = SCUtils.getPlayerData().scrapManager

        var bar = ScrapBar(manager, innerElement, 160f, 14f, scrapGain = scrapGain)
        //bar.position.inTL(70f, 0f)
        bar.position.rightOfMid(icon.elementPanel, 4f)
    }

}