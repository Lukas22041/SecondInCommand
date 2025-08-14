package second_in_command.skills.automated

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.kotlin.isAutomated
import org.magiclib.kotlin.isPirateFaction
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeAutomated : SCBaseAptitudePlugin() {

    companion object {

    }

    override fun addCodexDescription(tooltip: TooltipMakerAPI) {
        tooltip.addPara("This aptitude provides the capability of fielding Automated ships. " +
                "Automated ships have the unique benefit of being able to have AI Cores slotted in to them. " +
                "AI Cores when slotted in to ships act as strong officers, which further allow you to exceed the maximum capacity of 10 officers in your fleet.  ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Automated ships", "")

        tooltip.addSpacer(10f)

        tooltip.addPara("An effective automated fleet requires careful consideration of AI Core usage, and benefits from additional automated points and combat readiness increases from other aptitudes.")
    }

    override fun getOriginSkillId(): String {
        return "sc_automated_automated_ships"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_automated_magnetic_shielding")
        section1.addSkill("sc_automated_self_repair")
        //section1.addSkill("sc_automated_final_gambit")
        section1.addSkill("sc_automated_overclock")
        section1.addSkill("sc_automated_electronic_warfare")
        section1.addSkill("sc_automated_specialised_equipment")
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

    override fun getMarketSpawnweight(market: MarketAPI): Float {
        var weight = spec.spawnWeight
        if (market.faction.id == Factions.LUDDIC_PATH) weight *= 0.4f
        if (market.faction.id == Factions.LUDDIC_CHURCH) weight *= 0.6f
        if (market.faction.id == Factions.TRITACHYON) weight *= 1.25f
        return weight
    }

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        if (fleet.flagship?.isAutomated() == true) return Float.MAX_VALUE

        if (fleet.fleetData.membersListCopy.any { it.isAutomated() }) return 3f

        return 0f
    }

}