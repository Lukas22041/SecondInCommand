package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class OfficerTraining : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("+2 to maximum level of officers under your command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this executive officer is unassigned, any officer over the level limit will have some skills made inactive", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")
        tooltip.addPara("   - Inactive skills can be restored by re-assigning this officer", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")


    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("sc_officer_training", 2f)

    }


    override fun onActivation(data: SCData) {

        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("sc_officer_training", 2f)

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

    override fun onDeactivation(data: SCData) {

        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).unmodify("sc_officer_training")

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


    }


}