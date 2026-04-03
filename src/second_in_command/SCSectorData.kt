package second_in_command

class SCSectorData {

    fun readResolve() : SCSectorData {
        if (dmodData == null) dmodData = HashMap<String, List<String>>()
        return this
    }

    //Map of MemberIds to dmods they have.
    var dmodData = HashMap<String, List<String>>()

    var replacedAutomatedSkillWithAptitude = false

    var continiousRepairsDPSoFar = 0f

    var hoveredOverOfficerPickerHelp = false

    /** Persisted per-save flag: true once the player has completed or skipped the skill-screen tutorial. */
    var hasSeenTutorial = false
}