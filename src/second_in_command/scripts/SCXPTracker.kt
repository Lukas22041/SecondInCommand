package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import second_in_command.SCUtils
import shipmastery.util.MathUtils

class SCXPTracker : EveryFrameScript {

    var lastXP: Long? = null

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
       return true
    }

    override fun advance(amount: Float) {

        var player = Global.getSector().playerPerson
        var currentXP = player.stats.xp
        if (lastXP == null) {
            lastXP = currentXP
        }

        if (lastXP != currentXP) {
            var diff = currentXP - lastXP!!
            if (diff <= 0) diff = 0L
            if (diff >= Long.MAX_VALUE) diff = Long.MAX_VALUE

            if (diff > 0L) {
                //println("Gained $diff XP")
                for (officer in SCUtils.getPlayerData().getOfficersInFleet()) {
                    officer.addXP(diff.toFloat())
                }
            }

            lastXP = currentXP
        }

    }

}