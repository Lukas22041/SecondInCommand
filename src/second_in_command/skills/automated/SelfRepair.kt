package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.HullRestoration
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SelfRepair : SCBaseSkillPlugin() {

    var DMOD_CHANCE_MULT = 0.5f
    val REPAIR_RATE_BONUS = 30f

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships are almost always recoverable if lost in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Ships lost in combat are 50%% less likely to acquire d-mods", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% ship repair rate outside of combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {
            stats!!.dynamic.getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, HullRestoration.RECOVERY_PROB)
            stats.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, DMOD_CHANCE_MULT)

            stats.repairRatePercentPerDay.modifyPercent(id, REPAIR_RATE_BONUS)
            stats.baseCRRecoveryRatePercentPerDay.modifyPercent(id, REPAIR_RATE_BONUS)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}