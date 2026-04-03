package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class VanguardTactics : SCBaseSkillPlugin() {

    var maxSpeedBonus = 20f
    var maxManeuverBonus = 20f
    var maxVentBonus = 30f

    override fun getAffectsString(): String {
        return "all non-civilian frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxSpeedBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)
        val maneuverBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_maneuver", maxManeuverBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)
        val ventBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_vent", maxVentBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)

        tooltip.addPara("+${speedBonus.toInt()}%% top speed (maximum: +${maxSpeedBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${maneuverBonus.toInt()}%% maneuverability (maximum: +${maxManeuverBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${ventBonus.toInt()}%% vent rate (maximum: +${maxVentBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addFrigateDestroyerDPThresholdInfo(tooltip, data.fleet.fleetData, data.commander.stats)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (SCThresholds.isCivilian(stats)) return
        if (hullSize != ShipAPI.HullSize.FRIGATE && hullSize != ShipAPI.HullSize.DESTROYER) return

        val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxSpeedBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)
        val maneuverBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_maneuver", maxManeuverBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)
        val ventBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_vent", maxVentBonus, SCThresholds.ThresholdBonusType.FRIGATE_DESTROYER_DP)

        stats!!.maxSpeed.modifyPercent(id, speedBonus)
        stats.maxTurnRate.modifyPercent(id, maneuverBonus)
        stats.turnAcceleration.modifyPercent(id, maneuverBonus)
        stats.acceleration.modifyPercent(id, maneuverBonus)
        stats.ventRateMult.modifyPercent(id, ventBonus)
    }
}
