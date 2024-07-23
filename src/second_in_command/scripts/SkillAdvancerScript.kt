package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import second_in_command.SCUtils

class SkillAdvancerScript : EveryFrameScript {

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }


    override fun advance(amount: Float) {
        if (!SCUtils.getSCData().isModEnabled) return

        for (skill in SCUtils.getSCData().getAllActiveSkillsPlugins()) {
            skill.advance(amount)
        }
    }

}