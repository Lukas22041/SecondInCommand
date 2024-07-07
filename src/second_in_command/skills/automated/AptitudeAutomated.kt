package second_in_command.skills.automated

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
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


}