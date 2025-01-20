package second_in_command.interactions.rules

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.RuleBasedDialog
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import org.lwjgl.input.Keyboard
import second_in_command.SCUtils
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.tooltips.SCSkillTooltipCreator


class SCOfficerHireDialog : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {
        dialog.optionPanel.clearOptions()

        var plugin = SCOfficerHireDialogDelegate(dialog.plugin, dialog.interactionTarget.activePerson)
        dialog.plugin = plugin
        plugin.init(dialog)

        return true
    }
}

class SCOfficerHireDialogDelegate(var original: InteractionDialogPlugin, var person: PersonAPI) : InteractionDialogPlugin {

    lateinit var dialog: InteractionDialogAPI



    override fun init(dialog: InteractionDialogAPI) {

        this.dialog = dialog

        dialog.optionPanel.clearOptions()

        dialog.optionPanel.addOption("\"Depends. What can you do?\"", "sc_convo_question")

        dialog.optionPanel.addOption("End the conversation", "sc_convo_end")
        dialog.optionPanel.setShortcut("sc_convo_end", Keyboard.KEY_ESCAPE, false, false, false, true)
    }

    fun returnToPrevious() {
        dialog.optionPanel.clearOptions()
        dialog.textPanel.addPara("End the conversation", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())
        dialog.textPanel.addPara("You cut the comm-link.")

        dialog.plugin = original
        dialog.visualPanel.hideFirstPerson()
        dialog.interactionTarget.activePerson = null
        (dialog.plugin as RuleBasedDialog).notifyActivePersonChanged()

        FireAll.fire(null, dialog, memoryMap, "PopulateOptions")
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        var aptitudeId = person.memoryWithoutUpdate.getString("\$sc_officer_aptitude")
        var aptitudePlugin = SCSpecStore.getAptitudeSpec(aptitudeId)!!.getPlugin()

        var credits = Global.getSector().playerFleet.cargo.credits
        var cost = 10000f

        if (optionData == "sc_convo_question") {
            dialog.textPanel.addPara("\"Depends. What can you do?\"", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            dialog.optionPanel.clearOptions()

            dialog.textPanel.addPara("You ask ${person.nameString} some questions to establish where ${person.hisOrHer} skills lie.")

            var scOfficer = SCOfficer(person, aptitudeId)


            SCUtils.showSkillOverview(dialog, scOfficer)

            dialog.textPanel.addPara("\"This is only an overview of what my kind of talent is capable of. At the start only the first skill will be active, but " +
                    "generally i will be able field the potential of 6 of those skills after i integrated myself in to your fleets workflow.",
                Misc.getTextColor(), Misc.getHighlightColor(), "6")

            var costString = Misc.getDGSCredits(cost)
            var creditsString = Misc.getDGSCredits(credits.get())

            dialog.textPanel.addPara("I'l expect a transaction of $costString credits, we can decide on the details of a monthly pay at a later date. " +
                    "Once the transfer is complete, i can be ready to board within the hour.\"", Misc.getTextColor(), Misc.getHighlightColor(), "$costString")

            dialog.textPanel.addPara("You have $creditsString credits available.", Misc.getTextColor(), Misc.getHighlightColor(), "$creditsString")

            dialog.optionPanel.addOption("Hire ${person.himOrHer}", "sc_convo_hire")

            if (credits.get() <= cost) {
                dialog.optionPanel.setEnabled("sc_convo_hire", false)
                dialog.optionPanel.setTooltip("sc_convo_hire", "Not enough credits.")
            }

            dialog.optionPanel.addOption("End the conversation", "sc_convo_end")
            dialog.optionPanel.setShortcut("sc_convo_end", Keyboard.KEY_ESCAPE, false, false, false, true)
        }

        if (optionData == "sc_convo_hire") {
            dialog.textPanel.addPara("Hire ${person.himOrHer}", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            var scOfficer = SCOfficer(person, aptitudeId)

            credits.subtract(cost)
            AddRemoveCommodity.addCreditsLossText(cost.toInt(), dialog.textPanel)
            dialog.textPanel.addParagraph("${person.nameString} (level ${scOfficer.getCurrentLevel()}) has joined your fleet",  Misc.getPositiveHighlightColor())

            SCUtils.getPlayerData().addOfficerToFleet(scOfficer)

            dialog.interactionTarget.market.commDirectory.removePerson(person)

            returnToPrevious()
        }

        if (optionData == "sc_convo_end") {
            returnToPrevious()
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