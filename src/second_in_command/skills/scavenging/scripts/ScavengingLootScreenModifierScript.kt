package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import second_in_command.SCUtils
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.getChildrenCopy
import second_in_command.ui.elements.ScrapWidget

class ScavengingLootScreenModifierScript : EveryFrameScript {

    var placedBar = false

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    companion object {
        var SCAVENGING_SCRAP_KEY = "\$SCAVENGING_SCRAP_KEY"
    }


    override fun advance(amount: Float) {

        var interactionDialog = Global.getSector().campaignUI.currentInteractionDialog ?: return

        if (SCUtils.getPlayerData().isAptitudeActive("sc_scavenging")) {

            //Dont continue if theres no scrap gain to display
            //var scrapGainedListener = Global.getSector().listenerManager.getListeners(ScavengingScrapLootListener::class.java).firstOrNull() ?: return
            //var scrapGained = scrapGainedListener.lastScrapGainedForLootScreen ?: return

            var scrapGained = interactionDialog.interactionTarget?.memoryWithoutUpdate?.get(SCAVENGING_SCRAP_KEY) as Float? ?: return

            var state = AppDriver.getInstance().currentState
            if (state !is CampaignState) return

            var core = ReflectionUtils.invoke("getCore", state)

            var dialog = ReflectionUtils.invoke("getEncounterDialog", state)
            if (dialog != null)
            {
                core = ReflectionUtils.invoke("getCoreUI", dialog)
            }

            if (core is UIPanelAPI)
            {
                val cargoTab = ReflectionUtils.invoke("getCurrentTab", core) as UIPanelAPI?
                if (cargoTab is UIPanelAPI) {
                    var inner = cargoTab.getChildrenCopy().first()
                    if (inner is UIPanelAPI) {

                        var children = inner.getChildrenCopy()
                        var leftPanel = children.filter { it is UIPanelAPI }.first()

                        if (leftPanel is UIPanelAPI) {

                            var lastButton = leftPanel.getChildrenCopy().filter { it is ButtonAPI }.firstOrNull()
                            if (lastButton != null) {
                                //scrapGainedListener.lastScrapGainedForLootScreen = null
                                interactionDialog.interactionTarget.memoryWithoutUpdate.set(SCAVENGING_SCRAP_KEY, null)

                                var width = lastButton.position.width
                                var height = 11f
                                var panel = Global.getSettings().createCustom(width, height, null)
                                leftPanel.addComponent(panel)
                                //panel!!.position.inTL(0f, 40f)
                                panel.position.belowMid(lastButton, 30f)

                                var element = panel!!.createUIElement(width, height, false)
                                panel.addUIElement(element)
                                element.position.inTL(0f, 0f)

                                var data = ScrapManager(Global.getSector().playerFleet)

                                var widget = ScrapWidget(element, scrapGained)
                                widget.position.inTL(leftPanel.position.width/2-widget.width/2, 0f)
                            }


                        }


                    }
                }
            }
        }
    }
}