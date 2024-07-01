package second_in_command.specs

import second_in_command.ui.elements.SkillGapElement
import second_in_command.ui.elements.SkillWidgetElement

class SCAptitudeSection(var canChooseMultiple: Boolean, var requiredPreviousSkills: Int, var soundId: String) {
    private var skills = ArrayList<String>()

    var uiGap: SkillGapElement? = null
    var activeSkillsInUI = ArrayList<SkillWidgetElement>()
    var previousUISections = ArrayList<SCAptitudeSection>()

    fun addSkill(skillId: String) {
        skills.add(skillId)
    }

    fun getSkills() = ArrayList(skills)
}