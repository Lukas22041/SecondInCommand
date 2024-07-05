package second_in_command.specs

import com.fs.starfarer.api.characters.PersonAPI
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils

class SCOfficer(var person: PersonAPI, var aptitudeId: String) {

    companion object {
        var inactiveXPMult = 0.33f
        var baseXPPerLevel = 1000f
    }

    var activeSkillIDs = mutableSetOf<String>()

    var skillPoints = 1
    private var experiencePoints: Float = 0f
    private var level: Int = 1

    fun getAptitudeSpec() : SCAptitudeSpec {
        return SCSpecStore.getAptitudeSpec(aptitudeId)!!
    }

    fun getAptitudePlugin() : SCBaseAptitudePlugin {
        return getAptitudeSpec().getPlugin()
    }



    fun getSkillSpecs() : List<SCSkillSpec> {
        return activeSkillIDs.map { SCSpecStore.getSkillSpec(it)!! }
    }

    fun getSkillPlugins() : List<SCBaseSkillPlugin> {
        return getSkillSpecs().map { it.getPlugin() }
    }

    fun getMaxLevel() : Int = getAptitudePlugin().getMaxLevel()

    fun getXPMult() : Float {
        var mult = 1f
        if (!isActive()) mult *= inactiveXPMult

        return mult
    }

    fun getExperiencePoints() = experiencePoints

    fun getCurrentLevel() = level

    fun getRequiredXP() : Float {
        var mult = Math.pow(getAptitudePlugin().getXPRequiredPerLevelMult().toDouble(), level.toDouble()).toFloat()
        return baseXPPerLevel * mult
    }

    fun addXP(xp: Float) {
        var random = MathUtils.getRandomNumberInRange(0.95f, 1.05f)
        experiencePoints += xp * (getXPMult() * random)

        levelUpIfNeeded()
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

    fun isActive() : Boolean {
        return SCUtils.getSCData().getActiveOfficers().contains(this)
    }


}