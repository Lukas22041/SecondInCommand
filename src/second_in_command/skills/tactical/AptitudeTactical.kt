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
        return "sc_tactical_war_room"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "combat1")

        section1.addSkill("sc_tactical_sustain_tactics")
        section1.addSkill("sc_tactical_strike_tactics")
        section1.addSkill("sc_tactical_wing_tactics")
        section1.addSkill("sc_tactical_suppression_tactics")
        section1.addSkill("sc_tactical_phasespace_tactics")
        section1.addSkill("sc_tactical_vanguard_tactics")
        section1.addSkill("sc_tactical_bulwark_tactics")
        section1.addSkill("sc_tactical_anchor_tactics")
        addSection(section1)

        // Capstones: pick one
        var section2 = SCAptitudeSection(true, 3, "combat2")
        section2.addSkill("sc_tactical_distribution_tactics")
        section2.addSkill("sc_tactical_doctrine_tactics")
        addSection(section2)


    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 1f
    }



}