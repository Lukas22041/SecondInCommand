package second_in_command.skills.management

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeManagement : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_management_crew_training"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership1")
        section1.addSkill("sc_management_top_condition")
        section1.addSkill("sc_management_well_organized")
        section1.addSkill("sc_management_authority")
        section1.addSkill("sc_management_re_entry")
        section1.addSkill("sc_management_command_and_conquer")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 2, "leadership2")
        section2.addSkill("sc_management_officer_management")
        section2.addSkill("sc_management_officer_training")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "leadership5")
        section3.addSkill("sc_management_best_of_the_best")
        section3.addSkill("sc_management_in_good_hands")
        addSection(section3)

    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 1f
    }


}