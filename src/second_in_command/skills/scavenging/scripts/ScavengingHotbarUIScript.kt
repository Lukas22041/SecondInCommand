package second_in_command.skills.scavenging.scripts

import com.fs.graphics.Sprite
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.getChildrenCopy
import second_in_command.ui.elements.ScrapBar
import second_in_command.ui.elements.ScrapWidget
import java.awt.Color

class ScavengingHotbarUIScript : EveryFrameScript {

    var panel: CustomPanelAPI? = null
    var abilityPanel: UIPanelAPI? = null

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    var firstFrame = true

    override fun advance(amount: Float) {

        if (firstFrame) {
            firstFrame = false
            return
        }

        var state = AppDriver.getInstance().currentState
        if (state !is CampaignState) return

        //var core = state.core as UIPanelAPI
        var core = ReflectionUtils.invoke("getCore", state) as UIPanelAPI ?: return

        if (abilityPanel == null) {
            for (child in core.getChildrenCopy()) {
                if (child is UIPanelAPI && ReflectionUtils.hasVariableOfType(Sprite::class.java, child)) {
                    var sprite = ReflectionUtils.get(null, child, Sprite::class.java)
                    if (sprite is Sprite) {
                        var texId = ReflectionUtils.get("textureId", sprite)
                        if (texId is String) {
                            if (texId.contains("campaign_abilities.png")) {
                                abilityPanel = child;
                                //break
                            }
                        }
                    }
                }
            }
        }

        if (abilityPanel != null && panel == null) {

            var width = 200f
            var height = 11f
            panel = Global.getSettings().createCustom(width, height, null)
            abilityPanel!!.addComponent(panel)
            panel!!.position.inTL(-8f, -height -40f)

            var element = panel!!.createUIElement(width, height, false)
            panel!!.addUIElement(element)

            /*element.addLunaElement(width, height).apply {
                enableTransparency = true
            }*/

            var data = ScrapManager(Global.getSector().playerFleet)

           /* var scrapIconPath = "graphics/secondInCommand/scavenging/ui/scrap.png"
            var icon = element.addLunaSpriteElement(scrapIconPath, LunaSpriteElement.ScalingTypes.STRETCH_ELEMENT, 24f, 16f)
            icon.getSprite().color = Color(77,142,80,255)


            var bar = ScrapBar(data, element, 160f, 14f)
            //bar.position.inTL(70f, 0f)
            bar.position.rightOfMid(icon.elementPanel, 4f)*/

            ScrapWidget(element)
        }

        if (panel != null) {
            if (SCUtils.getPlayerData().isAptitudeActive("sc_scavenging")) {
                panel!!.opacity = 1f
            } else {
                panel!!.opacity = 0f
            }
        }

    }

}