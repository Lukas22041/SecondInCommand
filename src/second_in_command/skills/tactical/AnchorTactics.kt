package second_in_command.skills.tactical

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class AnchorTactics : SCBaseSkillPlugin() {

    var maxRangeBonus = 10f
    var maxSpeedBonus = 20f
    var maxManeuverBonus = 20f
    val auraRange = 1500f
    val auraRangeBonus = 10f

    override fun getAffectsString(): String {
        return "all non-civilian capitals"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val rangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxRangeBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
        val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxSpeedBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
        val maneuverBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_maneuver", maxManeuverBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
        val auraBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_aura", auraRangeBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)

        tooltip.addPara("+${speedBonus.toInt()} top speed (maximum: +${maxSpeedBonus.toInt()})", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${maneuverBonus.toInt()}%% maneuverability (maximum: +${maxManeuverBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${rangeBonus.toInt()}%% weapon range (maximum: +${maxRangeBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addCapitalDPThresholdInfo(tooltip, data.fleet.fleetData, data.commander.stats)

        tooltip.addSpacer(10f)

        tooltip.addPara("+${auraBonus.toInt()}%% weapon range to allied non-capital ships within %s units (maximum: +${auraRangeBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor(), "${auraRange.toInt()}")
        tooltip.addPara("   - Does not stack if multiple allied capitals are nearby", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Cannot apply to other allied capitals", 0f, Misc.getTextColor(), Misc.getHighlightColor())

        SCThresholds.addCapitalDPThresholdInfo(tooltip, data.fleet.fleetData, data.commander.stats)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (SCThresholds.isCivilian(stats)) return
        if (hullSize != ShipAPI.HullSize.CAPITAL_SHIP) return

        val rangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxRangeBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
        val speedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxSpeedBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
        val maneuverBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_maneuver", maxManeuverBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)

        stats!!.ballisticWeaponRangeBonus.modifyPercent(id, rangeBonus)
        stats.energyWeaponRangeBonus.modifyPercent(id, rangeBonus)
        stats.missileWeaponRangeBonus.modifyPercent(id, rangeBonus)

        stats.maxSpeed.modifyFlat(id, speedBonus)
        stats.maxTurnRate.modifyPercent(id, maneuverBonus)
        stats.turnAcceleration.modifyPercent(id, maneuverBonus)
        stats.acceleration.modifyPercent(id, maneuverBonus)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (ship == null) return
        if (SCThresholds.isCivilian(ship.mutableStats)) return
        // Non-capitals receive the aura receiver script; capitals emit the aura passively
        if (!ship.isCapital && !ship.hasListenerOfClass(AnchorTacticsAuraScript::class.java)) {
            val auraBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_aura", auraRangeBonus, SCThresholds.ThresholdBonusType.CAPITAL_DP)
            ship.addListener(AnchorTacticsAuraScript(ship, auraBonus))
        }
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        val dp = SCThresholds.getCapitalDP(fleet.fleetData, fleet.commander.stats)
        val multiplier = (dp / SCThresholds.CAPITAL_DP_THRESHOLD).coerceIn(0f, 1f)
        return super.getNPCSpawnWeight(fleet) * multiplier
    }
}

class AnchorTacticsAuraScript(val ship: ShipAPI, val rangeBonus: Float) : AdvanceableListener {

    companion object {
        const val BUFF_KEY = "sc_anchor_tactics_aura"
        const val AURA_RANGE = 1500f
    }

    val interval = IntervalUtil(0.2f, 0.2f)
    var isActive = false

    override fun advance(amount: Float) {
        if (ship == Global.getCombatEngine().playerShip && isActive) {
            Global.getCombatEngine().maintainStatusForPlayerShip(
                BUFF_KEY,
                "graphics/icons/hullsys/targeting_feed.png",
                "Anchor Tactics",
                "+${rangeBonus.toInt()}% Weapon Range",
                false
            )
        }

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        isActive = false

        val iterator = Global.getCombatEngine().shipGrid.getCheckIterator(ship.location, AURA_RANGE * 2f, AURA_RANGE * 2f)
        for (entry in iterator) {
            val other = entry as? ShipAPI ?: continue
            if (!other.isAlive) continue
            if (other.owner != ship.owner) continue
            if (!other.isCapital) continue
            if (MathUtils.getDistance(other, ship) >= AURA_RANGE) continue

            isActive = true
            break
        }

        if (isActive) {
            ship.mutableStats.ballisticWeaponRangeBonus.modifyPercent(BUFF_KEY, rangeBonus)
            ship.mutableStats.energyWeaponRangeBonus.modifyPercent(BUFF_KEY, rangeBonus)
            ship.mutableStats.missileWeaponRangeBonus.modifyPercent(BUFF_KEY, rangeBonus)
        } else {
            ship.mutableStats.ballisticWeaponRangeBonus.unmodify(BUFF_KEY)
            ship.mutableStats.energyWeaponRangeBonus.unmodify(BUFF_KEY)
            ship.mutableStats.missileWeaponRangeBonus.unmodify(BUFF_KEY)
        }
    }
}
