package second_in_command

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BattleAPI
import com.fs.starfarer.api.campaign.CampaignEventListener
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetInflater
import com.fs.starfarer.api.campaign.listeners.FleetEventListener
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

//Per Fleet Data
class SCData(var fleet: CampaignFleetAPI) : EveryFrameScript, FleetEventListener {

    var isNPC = false
    var isPlayer = false
    var faction = fleet.faction
    var commander = fleet.commander

    private var officers = ArrayList<SCOfficer>()
    private var activeOfficers = ArrayList<SCOfficer?>()

    init {

        //So that the fleet itself can advance its skills.
        fleet.addScript(this)

        if (commander == null) {
            commander = faction.createRandomPerson()
        }

        activeOfficers.add(null)
        activeOfficers.add(null)
        activeOfficers.add(null)

        fleet.addEventListener(this)

        officers.clear()

        isNPC = fleet != Global.getSector().playerFleet
        isPlayer = !isNPC

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
        addOfficerToFleet(person)
        setOfficerInSlot(0, person)

        //Generate portraits & name based on faction

    }

    fun getOfficersInFleet() : ArrayList<SCOfficer> {
        return ArrayList(officers)
    }

    fun addOfficerToFleet(officer: SCOfficer) {
        officer.data = this
        officers.add(officer)
    }

    fun removeOfficerFromFleet(officer: SCOfficer) {
        officer.data = null
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



    //Run deactivation on despawn
    override fun reportFleetDespawnedToListener(fleet: CampaignFleetAPI?, reason: CampaignEventListener.FleetDespawnReason?, param: Any?) {
        if (this.fleet == fleet) {
            var skills = getAllActiveSkillsPlugins()
            for (skill in skills) {
                skill.onDeactivation(this)
            }
        }
    }


    override fun reportBattleOccurred(fleet: CampaignFleetAPI?, primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {

    }


}