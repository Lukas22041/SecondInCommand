package second_in_command.skills.tactical

import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class DistributionTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: +33% to all diminishing return thresholds of invested tactical skills
        //       (does not affect Doctrine Tactics)
    }
}
