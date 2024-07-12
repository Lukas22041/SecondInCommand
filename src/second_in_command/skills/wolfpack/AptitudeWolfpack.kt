package second_in_command.skills.wolfpack

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeWolfpack : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_wolfpack_wolfpack_tactics"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership1")
        section1.addSkill("sc_wolfpack_quick_recovery")
        section1.addSkill("sc_wolfpack_low_profile")
        section1.addSkill("sc_wolfpack_coordinated_maneuvers")
        section1.addSkill("sc_wolfpack_jumpstart")
        section1.addSkill("sc_wolfpack_trapped_prey")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 2, "leadership2")
        section2.addSkill("sc_wolfpack_together_as_one")
        section2.addSkill("sc_wolfpack_leader_of_the_pack")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "leadership5")
        section3.addSkill("sc_wolfpack_support_doctrine")
        section3.addSkill("sc_wolfpack_quick_as_the_wind")
        addSection(section3)

    }

}