package second_in_command.skills.tactical.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.util.IntervalUtil

class StrikeTacticsScript(var ship: ShipAPI, var maxRegenTime: Float) : AdvanceableListener {

    val interval = IntervalUtil(maxRegenTime, maxRegenTime)

    override fun advance(amount: Float) {
        if (!ship.isAlive) return
        if (Global.getCombatEngine().isPaused) return

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        for (w in ship.allWeapons) {
            if (w.type != WeaponAPI.WeaponType.MISSILE) continue
            if (w.usesAmmo() && w.ammo < w.maxAmmo) {
                val restore = Math.max(1, (w.maxAmmo * 0.20f).toInt())
                w.ammo = (w.ammo + restore).coerceAtMost(w.maxAmmo)
            }
        }
    }
}
