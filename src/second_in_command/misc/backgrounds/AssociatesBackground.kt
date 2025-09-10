package second_in_command.misc.backgrounds

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionSpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import exerelin.campaign.backgrounds.BaseCharacterBackground
import exerelin.utilities.NexFactionConfig
import second_in_command.SCUtils
import second_in_command.misc.SCSettings
import second_in_command.misc.randomAndRemove
import second_in_command.specs.SCAptitudeSpec
import second_in_command.specs.SCBaseAptitudePlugin
import second_in_command.specs.SCSpecStore


class AssociatesBackground : BaseCharacterBackground() {

    override fun getShortDescription(factionSpec: FactionSpecAPI?, factionConfig: NexFactionConfig?): String {
        return "You are bound to experience the sector with a particular kind of deck crew, each with their own spin on fleet procedures."
    }

    override fun getLongDescription(factionSpec: FactionSpecAPI?, factionConfig: NexFactionConfig?): String {
        return "The associates you have made influence your fleet, in one way or another. Without them you would be nothing, " +
                "and you don't even consider to ever cut ties with any of them, even when accommodating for their strengths turns out to be difficult."
    }

    fun getTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addSpacer(10f)

        var hc = Misc.getHighlightColor()
        var nc = Misc.getNegativeHighlightColor()

        var text = "Only aptitudes that are available as a starting option can be randomly selected for this."
        if (SCSettings.unrestrictedAssociates!!) text = ""

        var label = tooltip.addPara(
                    "You start the game with three random executive officers of different aptitudes. Those officers can never be replaced or removed from your fleet. $text\n\n" +
                    "The players previous experience provides them with an additional skill point for their \"Combat\" aptitude. Due to the executive officers particular nature, their experience gain is reduced by 30%.\n\n" +
                    "This background is not recommended if this is your first time using the \"Second-in-Command\" mod. This start ignores the \"Progression Mode\" that can be enabled in the configs.", 0f)

        label.setHighlight("three random executive officers", "can never be replaced or removed", "additional skill point", "Combat", "30%", "This background is not recommended if this is your first time using the \"Second-in-Command\" mod." )
        label.setHighlightColors(hc, nc, hc, hc, nc, nc)

    }



    override fun addTooltipForSelection(tooltip: TooltipMakerAPI?, factionSpec: FactionSpecAPI?, factionConfig: NexFactionConfig?, expanded: Boolean) {
        super.addTooltipForSelection(tooltip, factionSpec, factionConfig, expanded)
        getTooltip(tooltip!!)
    }

    override fun addTooltipForIntel(tooltip: TooltipMakerAPI?, factionSpec: FactionSpecAPI?, factionConfig: NexFactionConfig?) {
        super.addTooltipForIntel(tooltip, factionSpec, factionConfig)
        getTooltip(tooltip!!)
    }

    override fun onNewGameAfterTimePass(factionSpec: FactionSpecAPI?, factionConfig: NexFactionConfig?) {
        var data = SCUtils.getPlayerData()

        var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }.filter { !it.tags.contains("restricted") }.toMutableList()
        if (!SCSettings.unrestrictedAssociates!!) {
            aptitudes = aptitudes.filter { it.tags.contains("startingOption") }.toMutableList() //Only pick aptitudes available from the starting interaction
        }

        var picks = ArrayList<SCBaseAptitudePlugin>()

        for (i in 0 until 3) {
            var pick = aptitudes.randomAndRemove()
            var categories = pick.categories

            //Remove aptitudes that share a category with this one
            for (cat in categories) {
                aptitudes = aptitudes.filter { it.categories.none { it == cat } }.toMutableList()
            }

            picks.add(pick)
        }

        for (pick in picks) {
            var officer = SCUtils.createRandomSCOfficer(pick.id)

            officer.person.memoryWithoutUpdate.set("\$sc_associatesOfficer", true)

            data.addOfficerToFleet(officer);
            data.setOfficerInEmptySlotIfAvailable(officer, true)
        }

        Global.getSector().characterData.person.stats.points += 1

        Global.getSector().memoryWithoutUpdate.set("\$sc_selectedStart", true) //Prevent Initial Hiring Dialog from showing up

        fillMissingSlot()
    }


    companion object {
        //Called if you have an associates run with 4XOs active
        fun fillMissingSlot() {
            var data = SCUtils.getPlayerData()

            if (!SCUtils.isAssociatesBackgroundActive()) return
            //if (!SCSettings.enable4thSlot) return
            var max = 3
            if (SCSettings.enable4thSlot) max = 4
            if (data.getAssignedOfficers().filterNotNull().size >= max) return

            var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }.filter { !it.tags.contains("restricted") }.toMutableList()
            if (!SCSettings.unrestrictedAssociates!!) {
                aptitudes = aptitudes.filter { it.tags.contains("startingOption") }.toMutableList() //Only pick aptitudes available from the starting interaction
            }

            aptitudes = aptitudes.filter { !data.hasAptitudeInFleet(it.id) }.toMutableList()

            var picks = ArrayList<SCBaseAptitudePlugin>()

            for (aptitude in aptitudes) {

                var valid = true

                for (category in aptitude.categories) {
                    for (active in data.getActiveOfficers()) {
                        if (active.getAptitudePlugin().categories.contains(category)) {
                            valid = false
                        }
                    }
                }
                if (valid) picks.add(aptitude)
            }

            var pick = picks.randomOrNull() ?: return

            var officer = SCUtils.createRandomSCOfficer(pick.id)
            officer.person.memoryWithoutUpdate.set("\$sc_associatesOfficer", true)

            data.addOfficerToFleet(officer);
            data.setOfficerInEmptySlotIfAvailable(officer, true)
        }
    }






}