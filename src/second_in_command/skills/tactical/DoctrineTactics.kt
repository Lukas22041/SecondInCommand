package second_in_command.skills.tactical

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class DoctrineTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all tactical threshold skills"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val hc = Misc.getHighlightColor()
        val tc = Misc.getTextColor()

        tooltip.addPara("Increases the thresholds of all tactics skills by %s",
            0f, hc, hc, "33%")

    }

    override fun onActivation(data: SCData) {
        if (data.fleet.fleetData != null) {
            data.fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.fleet.fleetData != null) {
            data.fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
    }
}
