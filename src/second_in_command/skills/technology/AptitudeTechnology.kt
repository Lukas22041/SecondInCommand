package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeTechnology : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_technology_flux_regulation"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership1")
        section1.addSkill("sc_technology_countermeasures")
        section1.addSkill("sc_technology_optimised_shields")
        section1.addSkill("sc_technology_deep_dive")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 2, "leadership2")
        section2.addSkill("sc_technology_reinforced_grid")
        section2.addSkill("sc_technology_phase_coil_tuning")
        section2.addSkill("sc_technology_focused_lenses")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "leadership2")
        section3.addSkill("sc_technology_makeshift_drones")
        section3.addSkill("sc_technology_neural_link")
        section3.addSkill("sc_technology_energised")
        addSection(section3)


    }

}