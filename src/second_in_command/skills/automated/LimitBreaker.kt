package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class LimitBreaker : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Increases the level of all AI cores by 1", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this officer is unassigned, the level is reduced back to the default", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If the core has more skills than possible at that level, it removes them automatically.", 0f, Misc.getTextColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {

        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }

    override fun advance(data: SCData, amount: Float) {
        for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
            var core = member.captain ?: continue

            if (core.isDefault) continue
            if (!core.isAICore) continue

            if (core.memoryWithoutUpdate.get("\$sc_limit_break_level") == true) return

            core.stats.level += 1
            core.memoryWithoutUpdate.set("\$sc_limit_break_level", true)
        }
    }

    override fun onDeactivation(data: SCData) {
        for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
            var core = member.captain ?: continue
            if (core.isDefault) continue
            if (!core.isAICore) continue

            if (core.memoryWithoutUpdate.get("\$sc_limit_break_level") == false) continue

            core.stats.level -= 1
            core.memoryWithoutUpdate.set("\$sc_limit_break_level", false)

            var skills = core.stats.skillsCopy
            var filtered = skills.filter { it.level > 0f && !it.skill.isAptitudeEffect }.toMutableList()

            if (filtered.count() <= core.stats.level) continue

            filtered = skills.filter {!it.skill.hasTag("npc_only") && !it.skill.hasTag("player_only") && !it.skill.hasTag("ai_core_only")}.toMutableList()

            if (filtered.isEmpty()) continue
            var last = filtered.last()
            core.stats.setSkillLevel(last.skill.id, 0f)

        }
    }

}