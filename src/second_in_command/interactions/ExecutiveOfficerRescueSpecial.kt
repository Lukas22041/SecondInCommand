package second_in_command.interactions

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageSpecialInteraction
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageSpecialInteraction.SalvageSpecialData
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer

class ExecutiveOfficerRescueSpecial(var officer: SCOfficer) : SalvageSpecialData {

    override fun createSpecialPlugin(): SalvageSpecialInteraction.SalvageSpecialPlugin {
        return ExecutiveOfficerRescueSpecialInteraction(officer)
    }
}

class ExecutiveOfficerRescueSpecialInteraction(var officer: SCOfficer) : BaseSalvageSpecial() {

    val OPEN = "open"
    val NOT_NOW = "not_now"

    override fun init(dialog: InteractionDialogAPI?, specialData: Any?) {
        super.init(dialog, specialData)
        text.addPara("While making a preliminary assessment, your salvage crews find a single occupied sleeper pod still running on backup power.")

        options.clearOptions()
        options.addOption("Attempt to open the pod", OPEN)
        options.addOption("Not now", NOT_NOW)
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        if (optionData == OPEN) {

            var plugin = officer.getAptitudePlugin()

            text.addPara("The thawing process completes, and the pod opens. " +
                    "It contains an executive officer, who joins you out of gratitude for being rescued.", Misc.getTextColor(), Misc.getHighlightColor(), "executive officer")

            text.setFontSmallInsignia()
            text.addParagraph("${officer.person.nameString} (level ${officer.getCurrentLevel()}) has joined your fleet",  Misc.getPositiveHighlightColor())
            text.setFontInsignia()

            text.addPara("${officer.person.heOrShe.capitalize()} continues by elaborating on ${officer.person.hisOrHer} skills, hoping they may be of use to you.", Misc.getTextColor(), plugin.getColor(), "${plugin.getName()}")

         /*   var tooltip = text.beginTooltip()

            tooltip.addPara("Aptitude: ${plugin.getName()}", 0f, Misc.getTextColor(), plugin.getColor(), "${plugin.getName()}")

            tooltip.addSpacer(10f)

            tooltip.addPara("\"${plugin.getDescription()}\"", 0f)
            text.addTooltip()*/

            SCUtils.showSkillOverview(dialog, officer)

            dialog.textPanel.addPara("\"This is only an overview of what my kind of talent is capable of. At the start only the first skill will be active, but " +
                    "generally i will be able field the potential of 6 of those skills after i integrated myself in to your fleets workflow.\"",
                Misc.getTextColor(), Misc.getHighlightColor(), "6")

            SCUtils.getPlayerData().addOfficerToFleet(officer)

            isDone = true
            setShowAgain(false)
        } else {
            isDone = true
            setEndWithContinue(false)
            setShowAgain(true)
        }

        //If Successful
       /* isDone = true
        setShowAgain(false)*/

        //If said "Not now"
        /*isDone = true
        setEndWithContinue(false)
        setShowAgain(true)*/
    }



}