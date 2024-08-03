package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FactionAPI
import second_in_command.specs.SCOfficer

object SCUtils {

    var MOD_ID = "second_in_command"
    var DATA_KEY = "\$sc_stored_data"

    @JvmStatic
    fun getPlayerData() : SCData {
        var data = Global.getSector().playerFleet.memoryWithoutUpdate.get(DATA_KEY) as SCData?
        if (data == null) {
            data = SCData(Global.getSector().playerFleet)
            Global.getSector().playerFleet.memoryWithoutUpdate.set(DATA_KEY, data)
        }
        return data
    }

    fun getFleetData(fleet: CampaignFleetAPI) : SCData{
        var data = fleet.memoryWithoutUpdate.get(DATA_KEY) as SCData?
        if (data == null) {
            data = SCData(fleet)
            fleet.memoryWithoutUpdate.set(DATA_KEY, data)
        }
        return data
    }

    @JvmStatic
    fun createRandomSCOfficer(aptitudeId: String) : SCOfficer {
        var person = Global.getSector().playerFaction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }

    @JvmStatic
    fun createRandomSCOfficer(aptitudeId: String, faction: FactionAPI) : SCOfficer {
        var person = faction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }

    /*@JvmStatic
    fun isSkillActive(skillId: String) : Boolean {
        return getPlayerData().isSkillActive(skillId)
    }*/

    fun changeOfficerAptitude(fleet: CampaignFleetAPI, officer: SCOfficer, aptitudeId: String) {
        var data = getFleetData(fleet)

        if (officer.isAssigned()) {
            var skills = officer.getActiveSkillPlugins()
            for (skill in skills) {
                skill.onDeactivation(data)
            }
        }
        officer.activeSkillIDs.clear()
        officer.resetLevel()
        officer.aptitudeId = aptitudeId

        if (officer.isAssigned()) {
            var skills = officer.getActiveSkillPlugins()
            for (skill in skills) {
                skill.onActivation(data)
            }
        }
    }

    @JvmStatic
    fun computeThresholdBonus(current: Float, maxBonus: Float, maxThreshold: Float): Float {

        var bonus = 0f
        var currValue = current
        var threshold = maxThreshold

        bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold)
        return bonus
    }

    @JvmStatic
    private fun getThresholdBasedRoundedBonus(maxBonus: Float, value: Float, threshold: Float): Float {
        var bonus = maxBonus * threshold / Math.max(value, threshold)
        if (bonus > 0 && bonus < 1) bonus = 1f
        if (maxBonus > 1f) {
            if (bonus < maxBonus) {
                bonus = Math.min(bonus, maxBonus - 1f)
            }
            bonus = Math.round(bonus).toFloat()
        }
        return bonus
    }

}