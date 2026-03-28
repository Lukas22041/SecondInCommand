package second_in_command.skills.tactical.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener

class StrikeTacticsScript(var ship: ShipAPI, var maxRegenTime: Float) : AdvanceableListener {

    var elapsed = 0f

    override fun advance(amount: Float) {
        if (!ship.isAlive) return
        if (Global.getCombatEngine().isPaused) return

        elapsed += amount

        val hasMissiles = ship.allWeapons.any { it.type == WeaponAPI.WeaponType.MISSILE && it.usesAmmo() }

        if (hasMissiles && ship == Global.getCombatEngine().playerShip) {
            val remaining = (maxRegenTime - elapsed).coerceAtLeast(0f)
            Global.getCombatEngine().maintainStatusForPlayerShip(
                "sc_strike_tactics",
                "graphics/icons/hullsys/ammo_feeder.png",
                "Strike Tactics",
                "${remaining.toInt()} seconds until reload",
                false
            )
        }

        if (elapsed < maxRegenTime) return
        elapsed -= maxRegenTime

        for (w in ship.allWeapons) {
            if (w.type != WeaponAPI.WeaponType.MISSILE) continue
            if (w.usesAmmo() && w.ammo < w.maxAmmo) {
                val restore = Math.max(1, (w.maxAmmo * 0.20f).toInt())
                w.ammo = (w.ammo + restore).coerceAtMost(w.maxAmmo)
            }
        }
    }
}
