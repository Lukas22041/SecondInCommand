package second_in_command.skills.support

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeSupport : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_support_fighter_uplink"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership2")
        section1.addSkill("sc_support_mobile_defenses")
        section1.addSkill("sc_support_huntsman")
        section1.addSkill("sc_support_carrier_group")
        section1.addSkill("sc_support_distanced_support")
        section1.addSkill("sc_support_system_proficiency")
        section1.addSkill("sc_support_swarm_deployment")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 4, "leadership4")
        section2.addSkill("sc_support_reconfiguration")
        section2.addSkill("sc_support_advanced_maneuvers")
        section2.addSkill("sc_support_barrage")
        addSection(section2)


    }

}