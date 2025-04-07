package second_in_command.skills.smallcraft

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeSmallcraft : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Smallcraft is an aptitude all about frigates and destroyers. " +
                "It is especially helpful for Wolfpack fleets, but can also come of use in any fleet with a lot of smaller ships. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Smallcraft", "")
    }

    override fun getOriginSkillId(): String {
        return "sc_smallcraft_wolfpack_tactics"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership1")
        section1.addSkill("sc_smallcraft_safe_recovery")
        section1.addSkill("sc_smallcraft_low_profile")
        section1.addSkill("sc_smallcraft_coordinated_maneuvers")
        section1.addSkill("sc_smallcraft_jumpstart")
        section1.addSkill("sc_smallcraft_trapped_prey")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 2, "leadership2")
        section2.addSkill("sc_smallcraft_together_as_one")
        section2.addSkill("sc_smallcraft_leader_of_the_pack")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 4, "leadership5")
        section3.addSkill("sc_smallcraft_support_doctrine")
        section3.addSkill("sc_smallcraft_quick_as_the_wind")
        addSection(section3)

    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        var mult = 0.25f
        var smallcraft = fleet.fleetData.membersListCopy.filter { it.isFrigate || it.isDestroyer }

        if (smallcraft.isEmpty()) return 0f

        var level = smallcraft.count().toFloat().levelBetween(0f, fleet.fleetData.membersListCopy.count().toFloat())

        if (level >= 0.3) {
            mult = 0.75f
        }

        if (level >= 0.5) {
            mult = 1.25f
        }

        if (level >= 0.75) {
            mult = 3f
        }

        return mult
    }

}