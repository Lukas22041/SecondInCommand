package second_in_command.skills.wolfpack

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class SafeRecovery : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {
        tooltip.addPara("+30%% to hull and armor damage resistance while venting", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% to the ships active vent rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advanceInCombat(ship: ShipAPI?, amount: Float) {

        if (!ship!!.isFrigate && !ship.isDestroyer) return

        if (ship.fluxTracker.isVenting) {
            ship.mutableStats.ventRateMult.modifyMult("sc_safe_recovery", 1.3f)
            ship.mutableStats.hullDamageTakenMult.modifyMult("sc_safe_recovery", 0.7f)
            ship.mutableStats.armorDamageTakenMult.modifyMult("sc_safe_recovery", 0.7f)
        }
        else {
            ship.mutableStats.ventRateMult.unmodify("sc_safe_recovery")
            ship.mutableStats.hullDamageTakenMult.unmodify("sc_safe_recovery")
            ship.mutableStats.armorDamageTakenMult.unmodify("sc_safe_recovery")
        }

    }

}