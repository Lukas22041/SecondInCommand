package second_in_command.skills.tactical

import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class WarRoom : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "officer capacity and command points"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: +1 maximum officers, +1 command point, 30% command point regen
    }
}
