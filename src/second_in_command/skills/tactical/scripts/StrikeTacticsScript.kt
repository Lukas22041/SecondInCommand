package second_in_command.skills.tactical.scripts

import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener

class StrikeTacticsScript(var ship: ShipAPI, var maxRegenTime: Float) : AdvanceableListener {

    override fun advance(amount: Float) {

    }
}