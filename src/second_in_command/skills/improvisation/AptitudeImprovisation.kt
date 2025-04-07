package second_in_command.skills.improvisation

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeImprovisation : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Improvisation provides the ability to make use out of d-mods. Their downside become easier to manage, and provide benefits for the fleet. " +
                "It also has some skills to provide benefits to hullmods that perform larger changes to how a ship works. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Improvisation", "")
    }

    override fun getOriginSkillId(): String {
        return "sc_improvisation_salvaged_armor"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "industry2")
        section1.addSkill("sc_improvisation_preservation")
        section1.addSkill("sc_improvisation_scrappy_maintenance")
        section1.addSkill("sc_improvisation_defensive_configuration")
        section1.addSkill("sc_improvisation_secured_mounts")
        section1.addSkill("sc_improvisation_mobilization")
        section1.addSkill("sc_improvisation_enhanced_overrides")
        section1.addSkill("sc_improvisation_redistribution")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 4, "industry4")
        section2.addSkill("sc_improvisation_derelict_fortifications")
        section2.addSkill("sc_improvisation_derelict_operations")
        addSection(section2)



    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        //Check for dmods

        var shipsWithDmods = fleet.fleetData.membersListCopy.filter { it.variant.hasDMods() }
        if (shipsWithDmods.isEmpty()) return 0f

        var mult = 0.25f
        var level = shipsWithDmods.count().toFloat().levelBetween(0f, fleet.fleetData.membersListCopy.count().toFloat())

        if (level >= 0.1) {
            mult = 1f
        }

        if (level >= 0.2) {
            mult = 1.25f
        }

        if (level >= 0.3) {
            mult = 1.5f
        }

        if (level >= 0.4) {
            mult = 2f
        }

        if (level >= 0.5) {
            mult = 3f
        }

        if (level >= 0.85) {
            mult = 4f
        }

        return mult
    }
}