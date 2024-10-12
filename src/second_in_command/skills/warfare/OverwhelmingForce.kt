package second_in_command.skills.warfare

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.combat.listeners.HullDamageAboutToBeTakenListener
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class OverwhelmingForce : SCBaseSkillPlugin() {


    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Disabling an opponent provides the ship with increased stats for 7 seconds", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ship gains 40%% increased flux dissipation", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "40%")
        tooltip.addPara("   - The ship gains 20%% hard flux dissipation while shields are active", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - Disabling another opponent while this effect is active resets its duration", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "resets")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {



    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

        var engine = Global.getCombatEngine() ?: return
        if (engine.listenerManager.getListeners(OverwhelmingForceDMGListener::class.java).none { (it as OverwhelmingForceDMGListener).side == ship!!.owner }) {
            engine.listenerManager.addListener(OverwhelmingForceDMGListener(ship!!.owner))
        }

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}

class OverwhelmingForceDMGListener(var side: Int) : HullDamageAboutToBeTakenListener {

    var maxTime = 7f

    override fun notifyAboutToTakeHullDamage(param: Any?, ship: ShipAPI, point: Vector2f?, damageAmount: Float): Boolean {

        if (param is ShipAPI) {
            //if (param != pilotedShip) return false
            if (ship.isFighter) return false
            if (ship.owner == side) return false
            if (ship.hitpoints <= 0 && !ship.hasTag("sc_force_counted")) {
                ship.addTag("sc_force_counted")
                //stacks.add(MomentumStacks(duration))

                var existing = ship.getListeners(OverwhelmingForceAdvanceListener::class.java).firstOrNull()

                if (existing != null) {
                    existing.duration = maxTime
                } else {
                    param.addListener(OverwhelmingForceAdvanceListener(param, maxTime))
                }

            }
        }

        return false
    }

}

class OverwhelmingForceAdvanceListener(var ship: ShipAPI, var duration: Float) : AdvanceableListener {

    override fun advance(amount: Float) {
        var stats = ship.mutableStats

        stats.fluxDissipation.modifyMult("sc_overwhelming_force", 1.5f)
        stats.hardFluxDissipationFraction.modifyFlat("sc_overwhelming_force", 0.2f)

        duration -= 1 * amount

        if (ship == Global.getCombatEngine().playerShip) {
            var remaining = duration.toInt()

            Global.getCombatEngine().maintainStatusForPlayerShip("sc_overwhelming_force", "graphics/icons/hullsys/damper_field.png",
                "Overwhelming Force", "Effect Active for ${remaining}s", false)
        }

        if (duration <= 0) {

            stats.fluxDissipation.unmodify("sc_overwhelming_force")
            stats.hardFluxDissipationFraction.unmodify("sc_overwhelming_force")

            ship.removeListener(this)
        }
    }
}