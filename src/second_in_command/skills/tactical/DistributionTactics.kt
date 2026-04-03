package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCSpecStore

class DistributionTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all non-acquired tactical threshold skills"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val hc = Misc.getHighlightColor()
        val tc = Misc.getTextColor()
        val distributionColor = java.awt.Color(200, 140, 50)

        tooltip.addPara("Activates all non-acquired tactics skills, but at only %s of their maximum threshold values.",
            0f, hc, hc, "50%")

        val distributedIds = data.getDistributionActivatedSkillIds()

        if (distributedIds.isNotEmpty()) {
            tooltip.addSpacer(10f)

            tooltip.addPara("Distributed skills:", 0f, tc, hc)
            for (skillId in distributedIds) {
                val spec = SCSpecStore.getSkillSpec(skillId) ?: continue
                tooltip.addPara("   - ${spec.name}", 0f, distributionColor, distributionColor)
            }
        }
    }

    /**
     * Returns the list of skill plugins that should be activated through distribution,
     * i.e. section-1 tactical skills not manually acquired by the officer.
     */
    private fun getDistributedPlugins(data: SCData): List<SCBaseSkillPlugin> {
        val distributedIds = data.getDistributionActivatedSkillIds()
        return distributedIds.mapNotNull { SCSpecStore.getSkillSpec(it)?.getPlugin() }
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        for (plugin in getDistributedPlugins(data)) {
            plugin.applyEffectsBeforeShipCreation(data, stats, variant, hullSize, "${id}_dist_${plugin.getId()}")
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI?, id: String) {
        for (plugin in getDistributedPlugins(data)) {
            plugin.applyEffectsAfterShipCreation(data, ship, variant, "${id}_dist_${plugin.getId()}")
        }
    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI, ship: ShipAPI, id: String) {
        for (plugin in getDistributedPlugins(data)) {
            plugin.applyEffectsToFighterSpawnedByShip(data, fighter, ship, "${id}_dist_${plugin.getId()}")
        }
    }

    override fun onActivation(data: SCData) {
        if (data.fleet.fleetData != null) {
            data.fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.fleet.fleetData != null) {
            data.fleet.fleetData.membersListCopy.forEach { it.updateStats() }
        }
    }
}
