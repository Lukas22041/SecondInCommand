package second_in_command.interactions.rules

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.OptionPanelAPI
import com.fs.starfarer.api.campaign.RuleBasedDialog
import com.fs.starfarer.api.campaign.TextPanelAPI
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll
import com.fs.starfarer.api.util.Misc

class SCStartBarEventDialog : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {
        dialog.optionPanel.clearOptions()

        var plugin = SCStartBarEventDialogDelegate(dialog.plugin)
        dialog.plugin = plugin
        plugin.init(dialog)

        return true
    }
}

class SCStartBarEventDialogDelegate(var original: InteractionDialogPlugin) : InteractionDialogPlugin {

    lateinit var dialog: InteractionDialogAPI
    lateinit var textPanel: TextPanelAPI
    lateinit var optionPanel: OptionPanelAPI


    override fun init(dialog: InteractionDialogAPI) {

        this.dialog = dialog
        this.textPanel = dialog.textPanel
        this.optionPanel = dialog.optionPanel

        textPanel.addPara("You approach the person that appears to be trying to sell of an assortment of contracts, though so far it appears to be to no avail.")

        textPanel.addPara("\"Hey, you there. You appear just like the kind of aspiring captain that will once be a ruling force of the sector, or some more moderate goal if that is to your liking!")

        textPanel.addPara("However, such goals are never reached without some support. May you be interested in what our company has to offer?\". Despite his energy, he appears to be somewhat nervous.")

        optionPanel.addOption("Hear him out on his offer", "HEAR_HIM_OUT")

        //optionPanel.addOption("Return", "RETURN")
    }

    fun returnToBar() {
        dialog.optionPanel.clearOptions()

        dialog.plugin = original
        dialog.visualPanel.hideFirstPerson()
        dialog.interactionTarget.activePerson = null
        (dialog.plugin as RuleBasedDialog).notifyActivePersonChanged()

        dialog.plugin.memoryMap.get(MemKeys.LOCAL)!!.set("\$option", "backToBar")
        FireAll.fire(null, dialog, memoryMap, "DialogOptionSelected")
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        if (optionData == "HEAR_HIM_OUT") {
            textPanel.addPara("")
        }

        if (optionData == "RETURN") {
            returnToBar()
        }

    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI> {
        return original.memoryMap
    }
}