package second_in_command.specs

import second_in_command.misc.VanillaSkillTooltip
import second_in_command.ui.elements.SkillGapElement
import second_in_command.ui.elements.SkillWidgetElement
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCAptitudeSection(var canChooseMultiple: Boolean, var requiredPreviousSkills: Int, var soundId: String) {
    private var skills = ArrayList<String>()

    var uiGap: SkillGapElement? = null
    var activeSkillsInUI = ArrayList<SkillWidgetElement>()
    var previousUISections = ArrayList<SCAptitudeSection>()

    var tooltips = ArrayList<SCSkillTooltipCreator>()
    var vanillaTooltips = ArrayList<VanillaSkillTooltip>()

    fun addSkill(skillId: String) {
        skills.add(skillId)
    }

    fun getSkills() = ArrayList(skills)
}