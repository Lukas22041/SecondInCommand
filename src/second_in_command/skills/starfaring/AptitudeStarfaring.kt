package second_in_command.skills.starfaring

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeStarfaring : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_starfaring_ad_astra"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "industry1")

        section1.addSkill("sc_starfaring_bulk_transport")
        section1.addSkill("sc_starfaring_salvaging")
        section1.addSkill("sc_starfaring_recovery_efforts")
        section1.addSkill("sc_starfaring_makeshift_equipment")
        //section1.addSkill("sc_starfaring_reactive_burn")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 1, "industry2")
        section2.addSkill("sc_starfaring_navigation")
        section2.addSkill("sc_starfaring_starmapping")
        section2.addSkill("sc_starfaring_emergency_order")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "industry4")
        section3.addSkill("sc_starfaring_expedition")
        section3.addSkill("sc_starfaring_continious_repairs")
        addSection(section3)



    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 0.33f
    }

}