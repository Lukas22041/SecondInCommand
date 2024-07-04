package second_in_command.skills.test

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class TestSkill : SCBaseSkillPlugin() {
    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {
        tooltip.addPara("This is a test skill", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

}