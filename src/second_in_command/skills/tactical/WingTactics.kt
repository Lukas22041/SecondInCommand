package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class WingTactics : SCBaseSkillPlugin() {

    var maxReplaceRate = 40f
    var maxEngagementRange = 20f
    var maxArmorBonus = 25f

    override fun getAffectsString(): String {
        return "all combat ships with fighter bays"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val replaceBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_replace", maxReplaceRate, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)
        val rangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxEngagementRange, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)
        val armorBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_armor", maxArmorBonus, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)

        tooltip.addPara("+${replaceBonus.toInt()}%% fighter replace rate (maximum: +${maxReplaceRate.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${rangeBonus.toInt()}%% fighter engagement range (maximum: +${maxEngagementRange.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${armorBonus.toInt()} effective armor to fighters (maximum: +${maxArmorBonus.toInt()})", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addFighterBaysCombatThresholdInfo(tooltip, data.fleet.fleetData)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (!SCThresholds.isCivilian(stats) && SCThresholds.hasFighterBays(stats)) {
            val replaceBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_replace", maxReplaceRate, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)
            val rangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxEngagementRange, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)

            stats!!.fighterRefitTimeMult.modifyMult(id, 1f - replaceBonus / 100f)
            stats.fighterWingRange.modifyPercent(id, rangeBonus)
        }
    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI, ship: ShipAPI, id: String) {

        if (SCThresholds.isCivilian(ship.mutableStats)) return

        val armorBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_armor", maxArmorBonus, SCThresholds.ThresholdBonusType.FIGHTER_BAYS_COMBAT)
        fighter.mutableStats.effectiveArmorBonus.modifyFlat(id, armorBonus)
    }
}
