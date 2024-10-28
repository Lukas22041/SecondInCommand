package second_in_command.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.input.InputEventAPI
import second_in_command.SCUtils
import second_in_command.skills.strikecraft.scripts.SynchronisedSkillScript

class CombatHandler : BaseEveryFrameCombatPlugin() {


    init {
        var engine = Global.getCombatEngine()
        if (engine != null) {
            if (Global.getCurrentState() != GameState.TITLE) {

                //engine.addLayeredRenderingPlugin(SynchronisedScript())

            }
        }
    }

    var addedSynchronised = false;

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {
        super.advance(amount, events)

        var engine = Global.getCombatEngine()
        if (engine != null) {
            if (Global.getCurrentState() != GameState.TITLE) {

                if (Global.getSector()?.playerFleet?.fleetData != null) {
                    if (!addedSynchronised && SCUtils.getPlayerData()?.isSkillActive("sc_strikecraft_synchronised") == true) {
                        addedSynchronised = true;
                        engine.addPlugin(SynchronisedSkillScript())
                    }
                }

            }
        }


    }

}