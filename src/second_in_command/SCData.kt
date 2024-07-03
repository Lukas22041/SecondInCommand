package second_in_command

import second_in_command.specs.SCOfficer

class SCData {

    private var officers = ArrayList<SCOfficer>()
    private var activeOfficers = ArrayList<SCOfficer?>()

    init {
        activeOfficers.add(null)
        activeOfficers.add(null)
        activeOfficers.add(null)

        officers.clear()
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude1"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude2"))
        officers.add(SCUtils.createRandomSCOfficer("sc_test_aptitude3"))
    }

    fun getOfficersInFleet() : ArrayList<SCOfficer> {
        return officers
    }

    fun getOfficerInSlot(slotIndex: Int) : SCOfficer? {
        return activeOfficers.getOrNull(slotIndex)
    }

    fun setOfficerInSlot(slotIndex: Int, officer: SCOfficer?) {
        activeOfficers[slotIndex] = officer
    }

    fun getActiveOfficers() : ArrayList<SCOfficer?> {
        return ArrayList(activeOfficers)
    }

}