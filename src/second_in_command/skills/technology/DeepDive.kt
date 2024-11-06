package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin

class DeepDive : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all phase ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+20%% peak performance time for phase ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("The active timeflow from phase-cloaks is increased by a flat 100%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The base increase in timeflow from a standard phase system is 300%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "300%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {


        stats!!.dynamic.getStat(Stats.PHASE_TIME_BONUS_MULT).modifyFlat(id, 1f)


        if (variant.baseOrModSpec().isPhase) {
            stats.peakCRDuration.modifyPercent(id, 20f)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {



    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.baseOrModSpec().isPhase }) return super.getNPCSpawnWeight(fleet)
        return 0f
    }

}