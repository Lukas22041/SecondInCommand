package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeTechnology : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Technical is an aptitude representing high-tech within the skill tree, however it is much more versatile than the name may suggest. " +
                "It is generaly effective in improving a fleets speed, use of energy weapons, shields and the performance of phase ships. " +
                "Its Makeshift Drones skill also enables the use of a few automated ships, without having to invest in to the whole Automation aptitude. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Technical", "Makeshift Drones")
    }

    override fun getOriginSkillId(): String {
        return "sc_technology_flux_regulation"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_technology_countermeasures")
        section1.addSkill("sc_technology_unlocked_engines")
        section1.addSkill("sc_technology_advanced_weaponry")
        section1.addSkill("sc_technology_deep_dive")
        section1.addSkill("sc_technology_reinforced_grid")

        addSection(section1)

        var section2 = SCAptitudeSection(true, 2, "technology2")
        section2.addSkill("sc_technology_optimised_shields")
        section2.addSkill("sc_technology_phase_coil_tuning")
        section2.addSkill("sc_technology_focused_lenses")
        addSection(section2)

        var section3 = SCAptitudeSection(true, 3, "technology4")
        //section3.addSkill("sc_technology_energised")
        section3.addSkill("sc_technology_neural_link")
        section3.addSkill("sc_technology_makeshift_drones")
        addSection(section3)


    }

    override fun getMarketSpawnweight(market: MarketAPI): Float {
        var weight = spec.spawnWeight
        if (market.faction.id == Factions.TRITACHYON) weight *= 1.25f
        return weight
    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        if (fleet.flagship?.baseOrModSpec()?.baseHullId == "ziggurat") return Float.MAX_VALUE

        var mult = 1f

        if (fleet.fleetData.membersListCopy.any { it.baseOrModSpec().isPhase }) mult += 0.5f

        return mult
    }



}