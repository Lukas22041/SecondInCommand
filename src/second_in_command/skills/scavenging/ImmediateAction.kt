package second_in_command.skills.scavenging

import com.fs.graphics.util.Fader
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin
import com.fs.starfarer.api.campaign.BaseStoryPointActionDelegate
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.OptionPanelAPI
import com.fs.starfarer.api.campaign.StoryPointActionDelegate
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import second_in_command.SCData
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.getChildrenCopy
import second_in_command.specs.SCBaseSkillPlugin
import java.awt.Color

class ImmediateAction : SCBaseSkillPlugin() {

    var OPTION = "IMMEDIATE_ACTION"
    var SCRAP_COST = 30f

    override fun getAffectsString(): String {
        return "dialog"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Adds an additional dialog option to fleet encounters, using it has the following effects", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - All ships current combat readiness is increased by 15%% for the encounter", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - All ships hull and armor is increased by 10%% for the encounter", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - The dialog option requires ${SCRAP_COST.toInt()}%% Scrap to use", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${SCRAP_COST.toInt()}%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {
        if (data.fleet.memoryWithoutUpdate.get("\$sc_immediate_action") == true) {
            //stats.maxCombatReadiness.modifyFlat(id, 0.15f)
            stats.armorBonus.modifyPercent(id, 10f)
            stats.hullBonus.modifyPercent(id, 10f)

           /* var member = stats.fleetMember
            if (member != null) {
                var maxCr = member.repairTracker.maxCR
                member.repairTracker.cr = MathUtils.clamp(member.repairTracker.cr + 0.15f, 0f, 1f)
            }*/
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

        var dialog = Global.getSector().campaignUI.currentInteractionDialog ?: return
        var plugin = dialog.plugin
        if (plugin !is FleetInteractionDialogPluginImpl) return

        if (dialog.optionPanel.hasOption(FleetInteractionDialogPluginImpl.OptionId.ENGAGE) && !dialog.optionPanel.hasOption(OPTION)) {

            //put above LEAVE, or at end of list.
            var optionIndex = dialog.optionPanel.savedOptionList.size - 1
            if (!dialog.optionPanel.hasOption(FleetInteractionDialogPluginImpl.OptionId.LEAVE)) {
                optionIndex = dialog.optionPanel.savedOptionList.size
            }

            var name = "Immediate action [${SCRAP_COST.toInt()}% Scrap]"
            dialog.optionPanel.addOption(name, OPTION, Color(77,142,80,255), null)



            val originalOptions = dialog.optionPanel.savedOptionList
            var optionAdded = false
            val newOptions: MutableList<Any?> = mutableListOf()
            for (i in 0 until originalOptions.size - 1) {
                if (i == optionIndex) {
                    newOptions.add(originalOptions.last())
                    optionAdded = true
                }
                newOptions.add(originalOptions[i])
            }

            if (!optionAdded) {
                newOptions.add(originalOptions.last())
            }

            dialog.optionPanel.restoreSavedOptions(newOptions)

            var panel = dialog.optionPanel as UIPanelAPI
            var children = panel.getChildrenCopy()
            for (option in children) {
                if (option is ButtonAPI) {
                    if (option.text.contains(name)) {
                        option.onClick {
                            onOptionSelected(dialog, data)
                        }
                    }
                }
            }

            var notEnoughScrap = data.scrapManager.getCurrentScrap() < SCRAP_COST

            var tooltipText = "Use scrap to improve your fleets combat readiness by 15% and hull and armor by 10% for the upcoming encounter. " +
                    "These improvements go beyond their usual maximum values."
            if (notEnoughScrap) {
                tooltipText += "\n\nYou do not have enough Scrap."
            }
            dialog.optionPanel.setTooltip(OPTION, tooltipText)
            dialog.optionPanel.setTooltipHighlights(OPTION, "15%", "10%", "You do not have enough Scrap.")
            dialog.optionPanel.setTooltipHighlightColors(OPTION, Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getNegativeHighlightColor())

            if (notEnoughScrap) {
                dialog.optionPanel.setEnabled(OPTION, false)
            }
        }


    }

    fun onOptionSelected(dialog: InteractionDialogAPI, data: SCData) {
        var optionPanel = dialog.optionPanel
        optionPanel.setEnabled(OPTION, false)
        Global.getSector().playerFleet.memoryWithoutUpdate.set("\$sc_immediate_action", true, 0.1f)
        Global.getSoundPlayer().playUISound(Sounds.STORY_POINT_SPEND, 1f, 1f)
        data.scrapManager.adjustScrap(-SCRAP_COST)

        for (member in data.fleet.fleetData.membersListCopy) {
            var maxCr = member.repairTracker.maxCR
            member.repairTracker.cr = MathUtils.clamp(member.repairTracker.cr + 0.15f, 0f, 1f)
        }
    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }



    //Code Below created by Starficz

    // CustomPanelAPI implements the same Listener that a ButtonAPI requires,
    // A CustomPanel then happens to trigger its CustomUIPanelPlugin buttonPressed() method
    // thus we can map our functions into a CustomUIPanelPlugin, and have them be triggered
    private class ButtonListener(var button: ButtonAPI) : BaseCustomUIPanelPlugin() {
        private val onClickFunctions = mutableListOf<() -> Unit>()

        var buttonListener = Global.getSettings().createCustom(0f, 0f, this)

        init {
            /*val setListenerMethod = ReflectionUtils.getMethodsOfName("setListener", button)[0]
            ReflectionUtils.rawInvoke(setListenerMethod, button, buttonListener)*/

            var method = ReflectionUtils.getMethod("setListener", button.javaClass, null, null, null)
            method!!.invoke(button, buttonListener)

            //ReflectionUtils.invoke("setListener", button, buttonListener)

        }

        override fun buttonPressed(buttonId: Any?) {
            onClickFunctions.forEach { it() }
        }

        fun addOnClick(function: () -> Unit) {
            onClickFunctions.add(function)
        }
    }

    // Extension function for ButtonAPI
    internal fun ButtonAPI.onClick(function: () -> Unit) {
        // Use reflection to check if this button already has a listener
        val existingListener = ReflectionUtils.invoke("getListener", this)
        /*if (existingListener is ButtonListener) {
            existingListener.addOnClick(function)
        } else {*/
            // if not, make one
            val listener = ButtonListener(this)
            listener.addOnClick(function)
        //}
    }





}