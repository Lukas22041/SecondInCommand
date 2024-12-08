package second_in_command.skills.special.christmas

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeChristmas : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_christmas_christmas_spirit"
    }

    override fun createSections() {
       /* var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_christmas_spirit")
        addSection(section1)*/
    }

    override fun getTags(): MutableList<String> {
        return mutableListOf("restricted")
    }

    override fun getNPCFleetSpawnWeight(data: SCData?, fleet: CampaignFleetAPI?): Float {
        return 0f
    }

}