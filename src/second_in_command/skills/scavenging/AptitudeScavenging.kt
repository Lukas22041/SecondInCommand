package second_in_command.skills.scavenging

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.Faction
import org.magiclib.kotlin.isAutomated
import org.magiclib.kotlin.isDecentralized
import org.magiclib.kotlin.isPirateFaction
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeScavenging : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Scavenging is a resource-management heavy aptitude that makes use of a resource exclusive to it, Scrap. " +
                "Officers with this aptitude can most commonly be found on independent worlds.",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Scavenging", "Scrap", "independent")
    }

    override fun getOriginSkillId(): String {
        return "sc_scavenging_another_mans_treasure"
    }

    override fun createSections() {
        var section1 = SCAptitudeSection(true, 0, "industry1")
        section1.addSkill("sc_scavenging_field_recycling")
        section1.addSkill("sc_scavenging_makeshift_measures")
        section1.addSkill("sc_scavenging_scrapforge_constructs")
        section1.addSkill("sc_scavenging_explorer")
        section1.addSkill("sc_scavenging_pinpointing")
        section1.addSkill("sc_scavenging_immediate_action")
        section1.addSkill("sc_scavenging_scrapheap")
        section1.addSkill("sc_scavenging_safe_transport")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 3, "industry2")
        section2.addSkill("sc_scavenging_hyperspatial_drifter")
        addSection(section2)
    }

    override fun getMarketSpawnweight(market: MarketAPI): Float {
        var weight = spec.spawnWeight
        if (market.faction.id == Factions.INDEPENDENT) weight *= 2f
        else if (market.faction.isDecentralized()) weight *= 1.2f
        else weight *= 0.8f
        return weight
    }

    //Currently not intended for NPCs
    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        return 0f
    }

}