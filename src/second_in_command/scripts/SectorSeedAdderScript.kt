/*
package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import lunalib.lunaExtensions.addLunaElement
import second_in_command.misc.*
import second_in_command.ui.elements.SeedDisplayElement

class SectorSeedAdderScript : EveryFrameScript {

    @Transient var panel: CustomPanelAPI? = null

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        if (!Global.getSector().isPaused) return

        var state = AppDriver.getInstance().currentState
        if (state !is CampaignState) return

        var screenPanel = ReflectionUtils.get("screenPanel", state) as UIPanelAPI ?: return
        var children = screenPanel.getChildrenCopy()

        for (child in children) {
            if (child is UIPanelAPI) {
                var innerChildren = child.getChildrenCopy()

                if (panel != null && innerChildren.contains(panel!!)) return

                for (innerChild in innerChildren) {
                    if (ReflectionUtils.hasMethodOfName("getCurr", innerChild)) {
                        var curr = ReflectionUtils.invoke("getCurr", innerChild)
                        if (curr is UIPanelAPI) {
                            var buttons = curr.getChildrenCopy()
                            if (buttons.any { it is ButtonAPI && it.text?.contains("Save") == true}) {

                                var parent = child
                                panel = Global.getSettings().createCustom(200f, 100f, null)
                                parent.addComponent(panel)
                                var element = panel!!.createUIElement(200f, 100f, false)
                                panel!!.addUIElement(element)

                                var seedElement = SeedDisplayElement(element, 200f, 30f).apply {
                                    enableTransparency = true

                                    var seed = Global.getSector().seedString

                                    innerElement.setParaFont("graphics/fonts/victor14.fnt")

                                    addText(seed, Misc.getBasePlayerColor())
                                    centerText()
                                }
                                panel!!.position.inTL(0f, 0f)
                                seedElement.position.inTL(35f, parent.getHeight() - seedElement.height - 5 - 10)





                                return
                            }
                        }
                    }
                }
            }
        }
    }
}*/
