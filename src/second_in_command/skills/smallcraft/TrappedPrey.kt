package second_in_command.skills.smallcraft

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class TrappedPrey : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "opposing ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Opposing ships surrounded by at least 2 frigates or destroyers without nearby allies gain several debuffs", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Their weapon recoil is increased by 20%%",0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - Their maximum speed is decreased by 15%%",0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - Their maneuvering speed is decreased by 15%%",0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - Their damage taken from all sources is increased by 5%%",0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        var owner = ship!!.owner
        if (Global.getCombatEngine()?.customData?.containsKey("sc_added_trapped_prey_$owner") == false) {
            Global.getCombatEngine()?.customData?.set("sc_added_trapped_prey_$owner", true)
            Global.getCombatEngine()?.addPlugin(TrappedPreyScript(/*ship!!.owner*/owner))
        }
    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

    }
}



class TrappedPreyScript(var owner: Int) : BaseEveryFrameCombatPlugin() {

    var interval = IntervalUtil(0.25f, 0.25f)

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {

        interval.advance(amount)

        if (!interval.intervalElapsed()) return

        for (ship in Global.getCombatEngine().ships) {
            if (ship.owner == owner) continue

            var count = 0

            var iterator = Global.getCombatEngine().shipGrid.getCheckIterator(ship.location, 2000f, 2000f)
            for (entry in iterator) {
                var target = entry as ShipAPI

                if (!target.isAlive) continue
                if (target.parentStation != null) continue

                //Check for nearby allies
                if (target.owner == ship.owner && target != ship) {
                    if (MathUtils.getDistance(ship, target) <= 1200f && !target.isFighter) {
                        count = 0
                        break
                    }
                    continue
                }



                if (!target.isFrigate && !target.isDestroyer) continue
                if (!isInRange(target, ship)) continue

                count += 1
            }

            if (count >= 2) {
                ship.mutableStats.recoilDecayMult.modifyMult("sc_trapped_prey", 0.8f)
                ship.mutableStats.recoilPerShotMult.modifyMult("sc_trapped_prey", 1.2f)
                ship.mutableStats.maxRecoilMult.modifyMult("sc_trapped_prey", 1.2f)

                ship.mutableStats.maxSpeed.modifyMult("sc_trapped_prey", 0.85f)
                ship.mutableStats.acceleration.modifyMult("sc_trapped_prey", 0.85f)
                ship.mutableStats.deceleration.modifyMult("sc_trapped_prey", 0.85f)
                ship.mutableStats.turnAcceleration.modifyMult("sc_trapped_prey", 0.85f)
                ship.mutableStats.maxTurnRate.modifyMult("sc_trapped_prey", 0.85f)

                ship.mutableStats.hullDamageTakenMult.modifyMult("sc_trapped_prey", 1.05f)
                ship.mutableStats.armorDamageTakenMult.modifyMult("sc_trapped_prey", 1.05f)
                ship.mutableStats.shieldDamageTakenMult.modifyMult("sc_trapped_prey", 1.05f)
            }
            else {
                ship.mutableStats.recoilDecayMult.unmodify("sc_trapped_prey")
                ship.mutableStats.recoilPerShotMult.unmodify("sc_trapped_prey")
                ship.mutableStats.maxRecoilMult.unmodify("sc_trapped_prey")

                ship.mutableStats.maxSpeed.unmodify("sc_trapped_prey")
                ship.mutableStats.acceleration.unmodify("sc_trapped_prey")
                ship.mutableStats.deceleration.unmodify("sc_trapped_prey")
                ship.mutableStats.turnAcceleration.unmodify("sc_trapped_prey")
                ship.mutableStats.maxTurnRate.unmodify("sc_trapped_prey")

                ship.mutableStats.hullDamageTakenMult.unmodify("sc_trapped_prey")
                ship.mutableStats.armorDamageTakenMult.unmodify("sc_trapped_prey")
                ship.mutableStats.shieldDamageTakenMult.unmodify("sc_trapped_prey")
            }

        }
    }

    fun isInRange(ship: ShipAPI, target: ShipAPI) : Boolean {
        var range = 0f
        for (weapon in ship.allWeapons) {
            if (weapon.type == WeaponAPI.WeaponType.MISSILE || weapon.type == WeaponAPI.WeaponType.SYNERGY) continue
            if (weapon.range > range)
            {
                range = weapon.range
            }
        }

        range = MathUtils.clamp(range, 0f, 1200f)

        var distance = MathUtils.getDistance(ship, target)
        var inRange = distance <= range
        return inRange
    }

}