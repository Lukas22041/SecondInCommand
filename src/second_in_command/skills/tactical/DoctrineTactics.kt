package second_in_command.skills.tactical

import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class DoctrineTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: enables all unspent tactical skills at 33% capacity,
        //       unaffected by Distribution Tactics but works with Anchor Tactics
    }
}
