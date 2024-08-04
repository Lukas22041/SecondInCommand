package second_in_command.skills.automated

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeAutomated : SCBaseAptitudePlugin() {

    companion object {

    }

    override fun getOriginSkillId(): String {
        return "sc_automated_automated_ships"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_automated_magnetic_shielding")
        section1.addSkill("sc_automated_self_repair")
        section1.addSkill("sc_automated_final_gambit")
        section1.addSkill("sc_automated_electronic_warfare")
        section1.addSkill("sc_automated_specialised_equipment")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 2, "technology3")
        section2.addSkill("sc_automated_deadly_persistence")
        section2.addSkill("sc_automated_wide_range")
        section2.addSkill("sc_automated_expertise")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "technology5")
        section3.addSkill("sc_automated_limit_breaker")
        section3.addSkill("sc_automated_neural_junction")
        addSection(section3)

    }

    override fun getNPCSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        var mult = 0.25f
        var carriers = fleet.fleetData.membersListCopy.filter { it.isCarrier || it.numFlightDecks >= 1 }

        if (carriers.isEmpty()) return 0f

        var level = carriers.count().toFloat().levelBetween(0f, fleet.fleetData.membersListCopy.count().toFloat())

        if (level >= 0.2) {
            mult = 1f
        }

        if (level >= 0.5) {
            mult = 2f
        }

        if (level >= 0.7) {
            mult = 5f
        }

        return mult
    }
}