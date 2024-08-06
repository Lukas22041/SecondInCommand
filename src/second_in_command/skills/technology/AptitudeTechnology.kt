package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeTechnology : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_technology_flux_regulation"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_technology_countermeasures")
        section1.addSkill("sc_technology_optimised_shields")
        section1.addSkill("sc_technology_deep_dive")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 2, "technology2")
        section2.addSkill("sc_technology_reinforced_grid")
        section2.addSkill("sc_technology_phase_coil_tuning")
        section2.addSkill("sc_technology_focused_lenses")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "technology4")
        section3.addSkill("sc_technology_energised")
        section3.addSkill("sc_technology_neural_link")
        section3.addSkill("sc_technology_makeshift_drones")
        addSection(section3)


    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        if (fleet.flagship?.baseOrModSpec()?.baseHullId == "ziggurat") return Float.MAX_VALUE

        var mult = 1f

        if (fleet.fleetData.membersListCopy.any { it.baseOrModSpec().isPhase }) mult += 0.5f

        return mult
    }

}