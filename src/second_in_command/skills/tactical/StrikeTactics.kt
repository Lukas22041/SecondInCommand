package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.skills.tactical.scripts.StrikeTacticsScript
import second_in_command.specs.SCBaseSkillPlugin

class StrikeTactics : SCBaseSkillPlugin() {

    var thresholdLimit = 90
    var baseRegenTime = 60 //Seconds, increases if you are over the threshold, 60s at threshold or above, 120s at twice over threshold, and so forth
    var maxMissileSpeed = 20f

    override fun getAffectsString(): String {
        return "all combat ships, including carriers and militarized civilian ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val hc = Misc.getHighlightColor()
        val tc = Misc.getTextColor()

        val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxMissileSpeed, SCThresholds.ThresholdBonusType.MISSILE_WEAPON_OP)

        tooltip.addPara("+${speedBonus.toInt()}%% missile speed (maximum: +${maxMissileSpeed.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Periodically restores %s, or at least 1, of missiles to all missile weapons on combat ships",
            0f, hc, hc, "20%")

        val missilePoints = SCThresholds.getMissileWeaponPoints(data.fleet.fleetData)
        val effectiveThreshold = SCThresholds.getEffectiveTacticalThreshold(SCThresholds.ThresholdBonusType.MISSILE_WEAPON_OP, data.fleet.fleetData)
        val regenTime = (baseRegenTime * Math.max(missilePoints, effectiveThreshold) / effectiveThreshold).toInt()

        tooltip.addPara("   - Reload interval: %s seconds (minimum: %s, increases above threshold)",
            0f, tc, hc,
            "" + regenTime,
            "" + baseRegenTime)

        SCThresholds.addMissileWeaponOPThresholdInfo(tooltip, data.fleet.fleetData)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (!SCThresholds.isCivilian(stats)) {
            val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxMissileSpeed, SCThresholds.ThresholdBonusType.MISSILE_WEAPON_OP)
            stats!!.missileMaxSpeedBonus.modifyPercent(id, speedBonus)
            stats.missileMaxTurnRateBonus.modifyPercent(id, speedBonus)
            stats.missileMaxTurnRateBonus.modifyPercent(id, speedBonus)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI?, id: String) {
        if (SCThresholds.isCivilian(ship.mutableStats)) return

        val missilePoints = SCThresholds.getMissileWeaponPoints(data.fleet.fleetData)
        val effectiveThreshold = SCThresholds.getEffectiveTacticalThreshold(SCThresholds.ThresholdBonusType.MISSILE_WEAPON_OP, data.fleet.fleetData)
        val regenTime = baseRegenTime * Math.max(missilePoints, effectiveThreshold) / effectiveThreshold
        ship.addListener(StrikeTacticsScript(ship, regenTime))
    }
}
