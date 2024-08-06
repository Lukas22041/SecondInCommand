package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Skills
import second_in_command.SCUtils

//Disable skills that activate due to other mods, this applies to anything that could appear on the skill screen and is not part of the combat aptitude
class VanillaSkillsDisabler() : EveryFrameScript {

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    var blacklistCombat = listOf<String>(
        "",
    )

    var blacklistLeadership = listOf<String>(
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
    )

    var blacklistTech = listOf<String>(
        "navigation",
        "sensors",
        //"gunnery_implants",
        //"energy_weapon_mastery",
        "electronic_warfare",
        "flux_regulation",
        "cybernetic_augmentation",
        "phase_corps",
        "neural_link",
        "automated_ships",
    )

    var blacklistIndustry = listOf<String>(
        "bulk_transport",
        "salvaging",
        "field_repairs",
        //"ordnance_expert",
        //"polarized_armor",
        "containment_procedures",
        "makeshift_equipment",
        "industrial_planning",
        "derelict_contingent",
        "hull_restoration",
    )

    var blacklist = blacklistCombat + blacklistLeadership + blacklistTech + blacklistIndustry

    override fun advance(amount: Float) {

        var activeSkills = Global.getSector().playerPerson.stats.skillsCopy
        activeSkills = activeSkills.filter { it.level >= 0.1 }

        for (skill in activeSkills) {
            if (blacklist.contains(skill.skill.id)) {
                skill.level = 0f

                if (skill.skill.id == Skills.AUTOMATED_SHIPS) {
                    if (!SCUtils.getSectorData().replacedAutomatedSkillWithAptitude) {
                        SCUtils.getSectorData().replacedAutomatedSkillWithAptitude = true

                        var data = SCUtils.getPlayerData()
                        var officer = SCUtils.createRandomSCOfficer("sc_automated")
                        data.addOfficerToFleet(officer)
                        data.setOfficerInEmptySlotIfAvailable(officer)
                    }
                }
            }
        }
    }
}