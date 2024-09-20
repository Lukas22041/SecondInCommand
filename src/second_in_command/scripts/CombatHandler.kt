package second_in_command.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.input.InputEventAPI
import second_in_command.skills.synchronised.scripts.SynchronisedScript

class CombatHandler : BaseEveryFrameCombatPlugin() {


    init {
        var engine = Global.getCombatEngine()
        if (engine != null) {
            if (Global.getCurrentState() != GameState.TITLE) {

                //engine.addLayeredRenderingPlugin(SynchronisedScript())

            }
        }
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {
        super.advance(amount, events)

    }

}