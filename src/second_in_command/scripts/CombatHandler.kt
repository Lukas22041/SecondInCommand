package second_in_command.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.sun.org.apache.bcel.internal.generic.RET
import second_in_command.skills.interlinked.scripts.InterlinkedScript

class CombatHandler : BaseEveryFrameCombatPlugin() {


    init {
        var engine = Global.getCombatEngine()
        if (engine != null) {
            if (Global.getCurrentState() != GameState.TITLE) {

                engine.addLayeredRenderingPlugin(InterlinkedScript())

            }
        }
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {
        super.advance(amount, events)

    }

}