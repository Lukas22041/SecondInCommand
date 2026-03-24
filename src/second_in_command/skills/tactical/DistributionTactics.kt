package second_in_command.skills.tactical

import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class DistributionTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "skills"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

    }
}
