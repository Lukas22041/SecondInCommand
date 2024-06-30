package second_in_command.specs

class SCAptitudeSection(var canChooseMultiple: Boolean, var requiredPreviousSkills: Int, soundId: String) {
    private var skills = ArrayList<String>()

    fun addSkill(skillId: String) {
        skills.add(skillId)
    }
}