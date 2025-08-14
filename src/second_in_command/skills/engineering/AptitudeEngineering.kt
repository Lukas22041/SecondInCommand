package second_in_command.skills.engineering

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.Faction
import org.magiclib.kotlin.isAutomated
import org.magiclib.kotlin.isPirateFaction
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeEngineering : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Engineering is a logistical aptitude that provides multiple tools for more efficient fleet operation, but specialises in logistical effects with more direct combat use, while also providing some skills for combat itself.",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Engineering")
    }

    override fun getOriginSkillId(): String {
        return "sc_engineering_reliable_Engineering"
    }

    override fun createSections() {
        var section1 = SCAptitudeSection(true, 0, "industry1")
        section1.addSkill("sc_engineering_fuel_optimisation")
        section1.addSkill("sc_engineering_stealth_coating")
        section1.addSkill("sc_engineering_compact_storage")
        section1.addSkill("sc_engineering_salvaging_equipment")
        section1.addSkill("sc_engineering_cooling_systems")
        section1.addSkill("sc_engineering_shock_absorption")
        section1.addSkill("sc_engineering_resiliency")
        addSection(section1)

        var section2 = SCAptitudeSection(false, 3, "industry3")
        section2.addSkill("sc_engineering_solid_construction")
        section2.addSkill("sc_engineering_prepare_for_the_worst")
        addSection(section2)
    }

    override fun getMarketSpawnweight(market: MarketAPI): Float {
        var weight = spec.spawnWeight
        if (market.faction.id == Factions.PIRATES) weight *= 0.8f
        else if (market.faction.isPirateFaction()) weight *= 0.85f
        return weight
    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 0.6f
    }

}