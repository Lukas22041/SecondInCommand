package second_in_command.skills.strikecraft

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeStrikecraft : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_strikecraft_fighter_uplink"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership2")
        section1.addSkill("sc_strikecraft_mobile_defenses")
        section1.addSkill("sc_strikecraft_huntsman")
        section1.addSkill("sc_strikecraft_carrier_group")
        section1.addSkill("sc_strikecraft_distanced_support")
        section1.addSkill("sc_strikecraft_system_proficiency")
        section1.addSkill("sc_strikecraft_swarm_deployment")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 4, "leadership4")
        section2.addSkill("sc_strikecraft_reconfiguration")
        section2.addSkill("sc_strikecraft_advanced_maneuvers")
        section2.addSkill("sc_strikecraft_barrage")
        addSection(section2)


    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        var mult = 0.25f
        var carriers = fleet.fleetData.membersListCopy.filter { it.isCarrier || it.numFlightDecks >= 1 }

        if (carriers.isEmpty()) return 0f

        var level = carriers.count().toFloat().levelBetween(0f, fleet.fleetData.membersListCopy.count().toFloat())


       /* if (carriers.isNotEmpty()) {
            mult = 0.5f
        }*/

        if (level >= 0.1) {
            mult = 0.5f
        }

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