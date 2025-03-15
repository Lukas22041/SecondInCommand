package second_in_command.specs

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.SCSettings

class SCOfficer(var person: PersonAPI, var aptitudeId: String) {

    companion object {
        var inactiveXPMult = 0.33f
        var baseXPPerLevel = 1000f
    }

    var activeSkillIDs = mutableSetOf<String>()
    var commonSkillSlots = HashMap<Int, String?>() //index, skillid

    var skillPoints = 1
    private var experiencePoints: Float = 0f
    private var level: Int = 1

    var data: SCData? = null

    fun getAptitudeSpec() : SCAptitudeSpec {
        return SCSpecStore.getAptitudeSpec(aptitudeId)!!
    }

    fun getAptitudePlugin() : SCBaseAptitudePlugin {
        return getAptitudeSpec().getPlugin()
    }

    fun setCommonInSlot(slot: Int, skillId: String) {

        if (data != null) {
            for (officer in data!!.getOfficersInFleet()) {
                for ((index, skill) in HashMap(officer.commonSkillSlots)) {
                    if (skill == skillId) {
                        if (officer.isAssigned()) {
                            var plugin = SCSpecStore.getSkillSpec(skillId)!!.getPlugin()
                            plugin.onDeactivation(data)
                        }
                        officer.commonSkillSlots[index] = null
                    }
                }
            }
        }

        commonSkillSlots.set(slot, skillId)

        var plugin = SCSpecStore.getSkillSpec(skillId)!!.getPlugin()
        plugin.onActivation(data)
    }


    fun addSkill(skillId: String) {
        activeSkillIDs.add(skillId)

        if (data != null && isAssigned()) {
            var plugin = SCSpecStore.getSkillSpec(skillId)!!.getPlugin()
            plugin.onActivation(data!!)
        }
    }



    fun getSkillSpecs() : List<SCSkillSpec> {
        var ids = ArrayList(activeSkillIDs)
        for ((slot, skill) in commonSkillSlots) {
            if (skill != null) {
                ids.add(skill)
            }
        }

        return ids.map { SCSpecStore.getSkillSpec(it)!! }
    }

    /*fun getSkillPlugins() : List<SCBaseSkillPlugin> {
        return getSkillSpecs().map { it.getPlugin() }
    }*/

    fun getActiveSkillPlugins() : List<SCBaseSkillPlugin> {
        //Changed as this made the origin skill apply last
        //return getSkillSpecs().filter { activeSkillIDs.contains(it.id) }.map { it.getPlugin() } + getAptitudePlugin().getOriginSkillPlugin()

        var list = mutableListOf<SCBaseSkillPlugin>()
        list.add(getAptitudePlugin().getOriginSkillPlugin())
        list.addAll(getSkillSpecs().filter { activeSkillIDs.contains(it.id) }.map { it.getPlugin() })


        return list
    }

    fun getMaxLevel() : Int = SCSettings.getMaxLevel()

    fun getXPMult() : Float {
        var mult = 1f
        //mult *= 0.5f //Default reduction in XP, shouldnt be the same as the players gain
        mult *= 0.80f //Default reduction in XP, shouldnt be the same as the players gain
        mult *= SCSettings.xpGainMult

        var player = Global.getSector().playerPerson
        var playerLevel = player.stats.level
        var levelMult = 1f
        if (playerLevel >= 2) levelMult += 0.1f
        if (playerLevel >= 4) levelMult += 0.1f
        if (playerLevel >= 6) levelMult += 0.1f
        if (playerLevel >= 8) levelMult += 0.2f
        mult *= levelMult

        if (SCUtils.isAssociatesBackgroundActive()) mult *= 0.70f; //Reduces XP for “Associates” background

        if (!isAssigned()) mult *= inactiveXPMult
        return mult
    }

    fun getExperiencePoints() = experiencePoints

    fun getCurrentLevel() = level

    fun getRequiredXP() : Float {
        var required = SCSettings.xpPerLevel.getOrNull(level) ?: 0f
        return required
    }

    fun addXP(xp: Float) {
        experiencePoints += xp * getXPMult()

        levelUpIfNeeded()
    }

    fun resetLevel() {
        level = 1
        skillPoints = 1
        experiencePoints = 0f
    }

    fun increaseLevel(amount: Int) {
        for (i in 0 until amount) {
            if (level >= getMaxLevel()) break
            skillPoints += 1
            level += 1
        }
        experiencePoints = 0f
    }

    fun levelUpIfNeeded() {
        var required = getRequiredXP()
        if (experiencePoints < required) return

        if (level >= getMaxLevel()) {
            experiencePoints = required
            return
        }

        var diff = experiencePoints - required
        diff = diff.coerceAtLeast(0f)

        experiencePoints = diff
        level += 1
        skillPoints += 1

        levelUpIfNeeded()
    }

    fun isAssigned() : Boolean {
        return data?.getAssignedOfficers()?.contains(this) ?: false
    }


}