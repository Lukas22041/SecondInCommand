package second_in_command

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BattleAPI
import com.fs.starfarer.api.campaign.CampaignEventListener
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.listeners.FleetEventListener
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.NPCOfficerGenerator
import second_in_command.skills.PlayerLevelEffects
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer

//Per Fleet Data
class SCData(var fleet: CampaignFleetAPI) : EveryFrameScript, FleetEventListener {

    var isNPC = false
    var isPlayer = false
    var faction = fleet.faction
    var commander = fleet.commander

    private var officers = ArrayList<SCOfficer>()
    private var activeOfficers = ArrayList<SCOfficer?>()

    init {


    }

    fun init() {

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
           /* var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }
            for (aptitude in aptitudes) {
                if (aptitude.getId(<<) == "sc_fake_combat_aptitude") continue

                var officer = SCUtils.createRandomSCOfficer(aptitude.getId())
                addOfficerToFleet(officer)
            }*/
        }
        else {

            clearCommanderSkills()

            if (!fleet.hasTag("sc_do_not_generate_skills")) {
                generateNPCOfficers()
            }

        }


        //Moved here, away from the inflator, in case this class only got created on interaction
        applyControllerHullmod()
    }



    fun getActiveOfficers() = activeOfficers.filterNotNull()

    fun generateNPCOfficers() {

        NPCOfficerGenerator.generateForFleet(this, fleet)

       /* var officer1 = SCUtils.createRandomSCOfficer("sc_tactical", faction)
        addOfficerToFleet(officer1)
        officer1.addSkill("sc_tactical_spotters")
        officer1.addSkill("sc_tactical_rapid_response")
        officer1.addSkill("sc_tactical_pristine_condition")
        setOfficerInSlot(0, officer1)

        var officer2 = SCUtils.createRandomSCOfficer("sc_wolfpack", faction)
        addOfficerToFleet(officer2)
        officer2.addSkill("sc_wolfpack_safe_recovery")
        officer2.addSkill("sc_wolfpack_low_profile")
        officer2.addSkill("sc_wolfpack_coordinated_maneuvers")
        officer2.addSkill("sc_wolfpack_jumpstart")
        officer2.addSkill("sc_wolfpack_trapped_prey")
        setOfficerInSlot(1, officer2)

        var officer3 = SCUtils.createRandomSCOfficer("sc_support", faction)
        addOfficerToFleet(officer3)
        officer3.addSkill("sc_support_huntsman")
        setOfficerInSlot(2, officer3)*/

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

    fun hasAptitudeInFleet(aptitudeId: String) : Boolean {
        return getOfficersInFleet().any { it.aptitudeId == aptitudeId }
    }

    fun setOfficerInEmptySlotIfAvailable(officer: SCOfficer) {

        //Check for incompatibilities
        var categories = officer.getAptitudePlugin().categories
        for (other in getActiveOfficers()) {
            var otherCategories = other.getAptitudePlugin().categories

            if (categories.any { otherCategories.contains(it) }) {
                return
            }
        }

        if (getOfficerInSlot(0) == null) {
            setOfficerInSlot(0, officer)
        }
        else if (getOfficerInSlot(1) == null) {
            setOfficerInSlot(1, officer)
        }
        else if (getOfficerInSlot(2) == null) {
            setOfficerInSlot(2, officer)
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

    fun getOfficersAssignedSlot(officer: SCOfficer) : Int? {
        if (!officer.isAssigned()) return null

        if (getOfficerInSlot(0) == officer) return 0
        if (getOfficerInSlot(1) == officer) return 1
        if (getOfficerInSlot(2) == officer) return 2

        return null
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

        if (isPlayer) {
            PlayerLevelEffects.advance(this, amount)
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

    fun applyControllerHullmod() {
        if (fleet.fleetData?.membersListCopy == null) return
        for (member in fleet.fleetData.membersListCopy) {
            if (!member.variant.hasHullMod("sc_skill_controller")) {
                if (member.variant.source != VariantSource.REFIT) {
                    var variant = member.variant.clone();
                    variant.originalVariant = null;
                    variant.hullVariantId = Misc.genUID()
                    variant.source = VariantSource.REFIT
                    member.setVariant(variant, false, true)
                }

                member.variant.addMod("sc_skill_controller")

                /*var moduleSlots = member.variant.moduleSlots
                for (slot in moduleSlots) {
                    var module = member.variant.getModuleVariant(slot)
                    module.addMod("sc_skill_controller")
                }*/
            }
        }
    }

    var blacklist = listOf(
        "tactical_drills",
        "coordinated_maneuvers",
        "wolfpack_tactics",
        "crew_training",
        "fighter_uplink",
        "carrier_group",
        "officer_training",
        "officer_management",
        "best_of_the_best",
        "support_doctrine",
        "electronic_warfare",
        "flux_regulation",
        "cybernetic_augmentation",
        "phase_corps",
        "derelict_contingent",
    )

    //Remove vanilla admiral skills from existing NPC fleets, mostly in case some mod overwrites the settings.json configs
    fun clearCommanderSkills() {


        var skills = commander.stats.skillsCopy.filter { it.level >= 0.1f }.filterNotNull()

        for (skill in ArrayList(skills)) {
            var id = skill.skill?.id ?: "skill_not_found"
            if (blacklist.contains(id)) {
                skill.level = 0f
                commander.stats.decreaseSkill(skill.skill.id)
            }
        }
    }

}