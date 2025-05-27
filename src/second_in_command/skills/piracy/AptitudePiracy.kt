package second_in_command.skills.piracy

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

class AptitudePiracy : SCBaseAptitudePlugin() {

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Piracy is a logistical aptitude that is more of a hybrid between providing logistical utility and some combat skills. " +
                "Compared to Starfaring it excels in providing you with more resources, but has far less convenience. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Piracy", "Starfaring")
    }

    override fun getOriginSkillId(): String {
        return "sc_piracy_generous_donation"
    }

    override fun createSections() {
        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_piracy_legitimate_salvage")
        section1.addSkill("sc_piracy_low_grade_deployment")
        section1.addSkill("sc_piracy_outmanoeuvred")
        section1.addSkill("sc_piracy_ambush")
        section1.addSkill("sc_piracy_stockpile")
        //section1.addSkill("sc_piracy_improvised_raids")
        section1.addSkill("sc_piracy_hunting_grounds")
        section1.addSkill("sc_piracy_all_out")
        section1.addSkill("sc_piracy_steadfast")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 3, "technology2")
        section2.addSkill("sc_piracy_bounty_board")
        section2.addSkill("sc_piracy_provisional_replacements")
        addSection(section2)
    }

    override fun getMarketSpawnweight(market: MarketAPI): Float {
        var weight = spec.spawnWeight
        if (market.faction.id == Factions.PIRATES) weight *= 3f
        else if (market.faction.isPirateFaction()) weight *= 2f
        return weight
    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        if (fleet.faction.id == Factions.PIRATES) return Float.MAX_VALUE
        if (fleet.faction.isPirateFaction()) return 2f
        return 0.33f
    }

}