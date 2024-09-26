package second_in_command.skills.improvisation

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class EnhancedOverrides : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships with the \"Safety Overrides\" hullmod gain the following effects: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ships peak performance time is improved by 25%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "25%")
        tooltip.addPara("   - Safety Overrides weapon range threshold is increased by 100 units", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "100")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (variant.hasHullMod(HullMods.SAFETYOVERRIDES)) {


            stats.peakCRDuration.modifyPercent(id, 25f)
            stats.weaponRangeThreshold.modifyFlat(id, 100f)

        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amunt: Float?) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.variant.hasHullMod(HullMods.SAFETYOVERRIDES)}) return super.getNPCSpawnWeight(fleet)
        return 0f
    }


}