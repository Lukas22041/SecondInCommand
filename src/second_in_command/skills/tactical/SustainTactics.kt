package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class SustainTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all combat ships, including carriers and militarized civilian ships"
    }

    var threshold = 120f
    var usageReductionMax = 15f;
    var dmodReductionMax = 20f;

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        val usagePercent = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_usage", usageReductionMax, SCThresholds.ThresholdBonusType.DP_LOW)
        val dmodPercent = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_dmod", dmodReductionMax, SCThresholds.ThresholdBonusType.DP_LOW)

        tooltip.addPara("Ships destroyed in combat have a ${dmodPercent.toInt()}%% chance to avoid d-mods (maximum: ${dmodReductionMax.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-${usagePercent.toInt()}%% supply usage (maximum: -${usageReductionMax.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-${usagePercent.toInt()}%% fuel usage (maximum: -${usageReductionMax.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        SCThresholds.addOPThresholdInfo(tooltip, data.fleet.fleetData, data.fleet.commander.stats, SCThresholds.getEffectiveTacticalThreshold(SCThresholds.ThresholdBonusType.DP_LOW, data.fleet.fleetData))

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (!SCThresholds.isCivilian(stats)) {
            val usagePercent = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_usage", usageReductionMax, SCThresholds.ThresholdBonusType.DP_LOW)
            val dmodPercent = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_dmod", dmodReductionMax, SCThresholds.ThresholdBonusType.DP_LOW)

            stats.suppliesPerMonth.modifyMult(id, 1f-usagePercent/100f)
            stats.fuelUseMod.modifyMult(id, 1f-usagePercent/100f)

            stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 1f-dmodPercent/100)

        }
    }

}
