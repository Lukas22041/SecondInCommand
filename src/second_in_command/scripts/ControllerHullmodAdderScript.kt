package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.util.IntervalUtil
import second_in_command.hullmods.SCControllerHullmod

class ControllerHullmodAdderScript : EveryFrameScript {

    override fun isDone(): Boolean {
        return false
    }


    override fun runWhilePaused(): Boolean {
        return true
    }

    var interval = IntervalUtil(0.2f, 0.2f)

    override fun advance(amount: Float) {
        interval.advance(amount)

        if (interval.intervalElapsed()) {
            SCControllerHullmod.ensureAddedControllerToFleet()
        }
    }

}