package second_in_command.skills.tactical

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeTactical : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Tactical is one of the more versatile aptitudes in the mod. " +
                "While it does not quite stand out much on its own, you can often use it for the purpose of finishing up your build. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Tactical", "")
    }

    override fun getOriginSkillId(): String {
        return "sc_tactical_focused_fire"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "combat1")
        section1.addSkill("sc_tactical_spotters")
        section1.addSkill("sc_tactical_rapid_response")
        section1.addSkill("sc_tactical_pristine_condition")
        section1.addSkill("sc_tactical_tactical_drills")
        section1.addSkill("sc_tactical_efficient_ordnance")
        //section1.addSkill("sc_tactical_superiority")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 3, "combat2")
        section2.addSkill("sc_tactical_full_throttle")
        section2.addSkill("sc_tactical_accelerated_barrels")
        section2.addSkill("sc_tactical_mass_bombardment")
        section2.addSkill("sc_tactical_defensive_protocols")
        addSection(section2)


    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 1f
    }



}