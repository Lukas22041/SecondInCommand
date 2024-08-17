package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.kotlin.getSalvageSeed
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.SCUtils.addAndCheckTag
import second_in_command.specs.SCBaseSkillPlugin
import java.util.*
import kotlin.collections.HashMap

class OfficerTraining : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships with officers"

    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
       /* tooltip.addPara("+2 to maximum level of officers under your command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this executive officer is unassigned, any officer over the level limit will have some skills made inactive", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")
        tooltip.addPara("   - Inactive skills can be restored by re-assigning this officer", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")*/

        tooltip.addPara("All ships with officers gain the following bonuses: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The range of all weapons is increased by 10%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - The ships resistance to all types of damage is increased 10%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var captain = stats!!.fleetMember?.captain
        if (captain != null && !captain.isDefault) {
            stats.ballisticWeaponRangeBonus.modifyPercent(id, 10f)
            stats.energyWeaponRangeBonus.modifyPercent(id, 10f)
            stats.missileWeaponRangeBonus.modifyPercent(id, 10f)

            stats.hullDamageTakenMult.modifyMult(id, 0.9f)
            stats.armorDamageTakenMult.modifyMult(id, 0.9f)
            stats.shieldDamageTakenMult.modifyMult(id, 0.9f)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

        //data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("sc_officer_training", 2f)

    }


    override fun onActivation(data: SCData) {

        /*data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("sc_officer_training", 2f)

        if (!data.isNPC) {
            var officers = Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }
            for (officer in officers) {


                var map = officer.memoryWithoutUpdate.get("\$sc_officer_training_inactive") as HashMap<String, Float>? ?: continue

                for ((skill, level) in map) {
                    officer.stats.setSkillLevel(skill, level)
                }

                officer.memoryWithoutUpdate.set("\$sc_officer_training_inactive", null)

                officer.stats.setSkillLevel("sc_inactive", 0f)
            }
        }

        //Increase officer levels and give skills
        if (data.isNPC && !data.fleet.addAndCheckTag("sc_officer_training_update")) {
            var levels = 2

            var membersWithOfficers = data.fleet.fleetData.membersListCopy
                .filter { (it.captain != null && !it.captain.isDefault) && !it.isAutomated() *//*&& !it.isFlagship*//*}.toMutableList() //Also filter out flagship, just to be save for some unique bounties

            //Filter out certain unique characters
            membersWithOfficers.filter { !Global.getSector().importantPeople.containsPerson(it.captain) }


            val plugin = Global.getSettings().getPlugin("officerLevelUp") as OfficerLevelupPlugin
            for (member in membersWithOfficers) {

                var captain = member.captain
                if (captain.hasTag("sc_officer_training_increased_lv")) continue //Do not increase the level multiple times

                captain.addTag("sc_officer_training_increased_lv")

                for (level in 0 until levels) {
                    captain.stats.level += 1

                    var skills = plugin.pickLevelupSkills(captain, Random(data.fleet.getSalvageSeed()))
                    if (skills.isNotEmpty()) {
                        var pick = skills.random()
                        captain.stats.increaseSkill(pick)
                    }
                }
            }

        }*/



    }

    override fun onDeactivation(data: SCData) {

        /*data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).unmodify("sc_officer_training")

        if (!data.isNPC) {
            var maxLevel = Global.getSector().characterData.person.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).computeEffective(Global.getSettings().getFloat("officerMaxLevel"))

            var officers = Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }
            for (officer in officers) {

                var anyRemoved = false

                if (officer.memoryWithoutUpdate.contains(MemFlags.OFFICER_MAX_LEVEL)) continue

                var map = HashMap<String, Float>()

                var stats = officer.stats

                var skills = officer.stats.skillsCopy
                var filtered = skills.filter { it.level > 0f && !it.skill.isAptitudeEffect }.toMutableList()

                filtered = filtered.filter { !it.skill.hasTag("npc_only") && !it.skill.hasTag("player_only") && !it.skill.hasTag("ai_core_only")}.toMutableList()

                while (filtered.count() > maxLevel) {
                    if (filtered.isEmpty()) break
                    anyRemoved = true

                    var last = filtered.last()
                    filtered.remove(last)

                    map.put(last.skill.id, last.level)

                    last.level = 0f
                }

                officer.memoryWithoutUpdate.set("\$sc_officer_training_inactive", map)

                if (anyRemoved) {
                    officer.stats.setSkillLevel("sc_inactive", 1f)
                }
            }
        }*/
    }


}