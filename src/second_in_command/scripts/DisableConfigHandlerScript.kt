package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.util.IntervalUtil
import second_in_command.SCUtils
import second_in_command.misc.SCSettings

class DisableConfigHandlerScript : EveryFrameScript {

    var interval = IntervalUtil(0.1f, 0.1f)

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {
        interval.advance(amount)

        var data = SCUtils.getSCData()

        if (data.isModEnabled && !SCSettings.isModEnabled) {
            data.isModEnabled = false

            var skills = data.getAllActiveSkillsPlugins()

            for (skill in skills) {
                skill.onDeactivation()
            }
            Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }

        if (!data.isModEnabled && SCSettings.isModEnabled) {
            data.isModEnabled = true

            var skills = data.getAllActiveSkillsPlugins()

            for (skill in skills) {
                skill.onActivation()
            }
            Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
    }
}