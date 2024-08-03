package second_in_command

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetInflater
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

//Per Fleet Data
class SCData(var fleet: CampaignFleetAPI) : EveryFrameScript {

    var isNPC = false
    var faction = fleet.faction

    private var officers = ArrayList<SCOfficer>()
    private var activeOfficers = ArrayList<SCOfficer?>()

    init {

        //So that the fleet itself can advance its skills.
        fleet.addScript(this)


        activeOfficers.add(null)
        activeOfficers.add(null)
        activeOfficers.add(null)


        officers.clear()

        isNPC = fleet != Global.getSector().playerFleet

        if (!isNPC) {
            //For Beta
            var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }
            for (aptitude in aptitudes) {
                if (aptitude.getId() == "sc_fake_combat_aptitude") continue

                var officer = SCUtils.createRandomSCOfficer(aptitude.getId())
                addOfficerToFleet(officer)
            }
        }
        else {
            generateNPCOfficers()
        }
    }

    fun generateNPCOfficers() {
        var person = SCUtils.createRandomSCOfficer("sc_tactical", faction)
        setOfficerInSlot(0, person)

        //Generate portraits & name based on faction

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
        var officerInSlot = getOfficerInSlot(slotIndex)
        activeOfficers[slotIndex] = officer

        if (officerInSlot != null) {
            var skills = officerInSlot.getActiveSkillPlugins()

            for (skill in skills) {
                skill.onDeactivation(this)
            }
            fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }

        if (officer != null) {
            var skills = officer.getActiveSkillPlugins()
            for (skill in skills) {
                skill.onActivation(this)
            }
            fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
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


    override fun isDone(): Boolean {
        return false
    }


    override fun runWhilePaused(): Boolean {
        return true
    }


    override fun advance(amount: Float) {

        for (skill in getAllActiveSkillsPlugins()) {
            skill.advance(this, amount)
        }

    }



}