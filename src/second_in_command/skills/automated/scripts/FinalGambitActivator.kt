package second_in_command.skills.automated.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DamageType
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipCommand
import com.fs.starfarer.api.combat.ShipwideAIFlags
import com.fs.starfarer.api.combat.listeners.HullDamageAboutToBeTakenListener
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.loading.DamagingExplosionSpec
import org.lazywizard.lazylib.combat.CombatUtils
import org.lwjgl.util.vector.Vector2f
import org.magiclib.kotlin.setAlpha
import org.magiclib.subsystems.MagicSubsystem
import java.awt.Color

class FinalGambitActivator(ship: ShipAPI) : MagicSubsystem(ship), HullDamageAboutToBeTakenListener {

    init {
        ship.addListener(this)
    }

    override fun getBaseActiveDuration(): Float {
        return 4f
    }

    override fun getBaseOutDuration(): Float {
        return 0.3f
    }

    override fun getBaseInDuration(): Float {
        return 1f
    }

    override fun getBaseCooldownDuration(): Float {
        return 1000f
    }

    override fun shouldActivateAI(amount: Float): Boolean {

        /*if (!ship.isAlive) return false
        if (isIn || isActive || isOut) return false
       // if (ship.fluxTracker.isOverloaded) return false


        var target = ship.shipTarget
        if (target == null) {
            var custom = ship.aiFlags.getCustom(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)
            if (custom is ShipAPI) {
                if (custom.owner != ship.owner) {
                    target = custom
                }
            }
        }

        if (target == null) {
            var ships = CombatUtils.getShipsWithinRange(ship.location, 2000f)
            ships = ships.filter { it.owner != ship.owner }

            target = ships.randomOrNull()
        }

        if (ship.hitpoints <= ship.maxHitpoints * 0.5f) {
            if (target != null) {
                ship.aiFlags.setFlag(ShipwideAIFlags.AIFlags.MOVEMENT_DEST, 1f, target.location)
                return true
            }
            *//*var target = ship.shipTarget
            if (target == null) {
                var custom = ship.aiFlags.getCustom(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)
                if (custom is ShipAPI) {
                    if (custom.owner != ship.owner) {
                        target = custom
                    }
                }
            }
            if (target != null) {
                *//**//*var isInArc = Misc.isInArc(ship.facing, 90f, ship.location, target.location)
                if (isInArc) {
                    return true
                }*//**//*
                return true
            }*//*
        }*/

        return false
    }



    override fun advance(amount: Float, isPaused: Boolean) {
        super.advance(amount, isPaused)

        if (!ship.isAlive) return

        var stats = ship.mutableStats

        stats.maxSpeed.modifyFlat("sc_final_gambit", 0f + (50f * effectLevel))
        stats.maxSpeed.modifyMult("sc_final_gambit", 1f + (0.5f * effectLevel))
        stats.acceleration.modifyMult("sc_final_gambit", 1f + (3f * effectLevel))
        stats.deceleration.modifyMult("sc_final_gambit", 1f + (3f * effectLevel))

        stats.turnAcceleration.modifyMult("sc_final_gambit", 1f - (0.2f * effectLevel))
        stats.maxTurnRate.modifyMult("sc_final_gambit", 1f - (0.2f * effectLevel))

        var jitterRangeBonus = 7f * effectLevel

        ship.setJitter(this, hudColor.setAlpha(55), effectLevel, 3, 0f, 0 + jitterRangeBonus)
        ship.setJitterUnder(this, hudColor.setAlpha(155), effectLevel, 25, 0f, 7f + jitterRangeBonus)

        if (isIn ||isActive || isOut) {
            ship.giveCommand(ShipCommand.ACCELERATE, null, 0)
            ship.blockCommandForOneFrame(ShipCommand.DECELERATE)
            ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS)

            ship.aiFlags.setFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS)
            ship.shield?.toggleOff()

            var target = ship.shipTarget

            if (target == null) {
                var ships = CombatUtils.getShipsWithinRange(ship.location, 2000f)
                ships = ships.filter { it.owner != ship.owner && !it.isHulk }

                target = ships.randomOrNull()
            }

            if (target != null) {
                ship.aiFlags.setFlag(ShipwideAIFlags.AIFlags.MOVEMENT_DEST, 1f, target.location)
            }

            Global.getSoundPlayer().playLoop("system_burn_drive_loop", ship, 1f, 1f * effectLevel, ship.location, ship.velocity)

            ship.engineController.extendFlame(this, 1f, 1f, 1f)
        }

        if (ship.hitpoints <= 100) {
            explode()
        }

        if (state == State.OUT) {
            explode()
        }
    }

    override fun onActivate() {
        super.onActivate()


        ship.engineController.shipEngines.forEach { it.repair() }
        ship.fluxTracker.stopOverload()
        ship.fluxTracker.currFlux = 0f
        ship.fluxTracker.hardFlux = 0f
        ship.mutableStats.dynamic.getStat(Stats.EXPLOSION_RADIUS_MULT).modifyMult("sc_final_gambit", 2.5f)
        ship.mutableStats.dynamic.getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyMult("sc_final_gambit", 1.25f)

        Global.getSoundPlayer().playSound("system_burn_drive_activate", 1f, 1f, ship.location, ship.velocity)

    }

    fun explode() {
       // if (!ship.isAlive) return
       // Global.getCombatEngine().applyDamage(ship, ship.location, 999999f, DamageType.ENERGY, 0f, true, false, null, false)

        var spec = DamagingExplosionSpec.explosionSpecForShip(ship)
        Global.getCombatEngine().spawnDamagingExplosion(spec, ship, ship.location, true)

        if (ship.isAlive) {
            Global.getCombatEngine().applyDamage(ship, ship.location, 999999f, DamageType.ENERGY, 0f, true, false, null, false)
        }
    }

    override fun getDisplayText(): String {
        return "Final Gambit"
    }

    override fun getHUDColor(): Color {
        return Color(0, 166, 175,255)
    }


    override fun notifyAboutToTakeHullDamage(param: Any?, ship: ShipAPI?, point: Vector2f?, damageAmount: Float): Boolean {

        if (isIn || isActive ) {
            return true
        }

        if (ship!!.hitpoints - damageAmount <= ship.maxHitpoints * 0.2f) {


            if (!isIn && !isActive && !isOut && !isCooldown && ship.isAlive) {
                activate()
            }

            if (!isOut) {
                ship.hitpoints = ship.maxHitpoints * 0.2f
                return true
            }
        }
        return false
    }

}