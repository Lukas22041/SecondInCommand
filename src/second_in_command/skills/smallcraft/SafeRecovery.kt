package second_in_command.skills.smallcraft

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SafeRecovery : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("+30%% to hull and armor damage resistance while venting", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% to the ships active vent rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

        if (!ship!!.isFrigate && !ship.isDestroyer) return

        if (ship.fluxTracker.isVenting) {

            if (ship == Global.getCombatEngine().playerShip) {

                Global.getCombatEngine().maintainStatusForPlayerShip("sc_jumpstart1", "graphics/icons/hullsys/entropy_amplifier.png",
                    "Safe Recovery", "+30% damage resistance", false)

                Global.getCombatEngine().maintainStatusForPlayerShip("sc_jumpstart2", "graphics/icons/hullsys/entropy_amplifier.png",
                    "Safe Recovery", "+30% vent rate", false)
            }

            ship.mutableStats.ventRateMult.modifyPercent("sc_safe_recovery", 30f)
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