package second_in_command.skills.common

import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class CommonSlotPlugin : SCBaseSkillPlugin() {
    override fun getAffectsString(): String {
        return ""
    }

    override fun addTooltip(data: SCData?, tooltip: TooltipMakerAPI?) {
        tooltip!!.addPara("Test", 0f)
    }

}