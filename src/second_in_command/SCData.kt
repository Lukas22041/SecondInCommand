package second_in_command

import com.fs.starfarer.api.characters.PersonAPI
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer

class SCData(var player: PersonAPI) {

    var isModEnabled = true

    private var officers = ArrayList<SCOfficer>()
    private var activeOfficers = ArrayList<SCOfficer?>()

    init {
        activeOfficers.add(null)
        activeOfficers.add(null)
        activeOfficers.add(null)

        officers.clear()
       /* officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude1"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude3"))*/
    }

    fun getOfficersInFleet() : ArrayList<SCOfficer> {
        return ArrayList(officers)
    }

    fun addOfficerToFleet(officer: SCOfficer) {
        officers.add(officer)
    }

    fun removeOfficerFromFleet(officer: SCOfficer) {
        officers.remove(officer)
    }

    fun getOfficerInSlot(slotIndex: Int) : SCOfficer? {
        return activeOfficers.getOrNull(slotIndex)
    }

    fun setOfficerInSlot(slotIndex: Int, officer: SCOfficer?) {
        activeOfficers[slotIndex] = officer
    }

    fun getAssignedOfficers() : ArrayList<SCOfficer?> {
        return ArrayList(activeOfficers)
    }

    fun getAllActiveSkillsPlugins() : List<SCBaseSkillPlugin> {
        return getAssignedOfficers().filter { it != null }.flatMap { it!!.getActiveSkillPlugins() }
    }

    fun isSkillActive(skillId: String) : Boolean {
        return getAssignedOfficers().filter { it != null }.flatMap { it!!.getActiveSkillPlugins().map { it.getId() } }.contains(skillId)
    }

}