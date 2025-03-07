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

class Mobilization : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Civilian and Militarised ships have a 25%% reduction in fuel usage.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Ships with the \"Militarized Subsystems\" hullmod gain the following effects: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ships top speed is increased by 10%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - The ships flux dissipation and capacity is increased by 10%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - The ships weapon range is increased by 15%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (variant.hasHullMod(HullMods.CIVGRADE) || variant.hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
            stats.fuelUseMod.modifyMult(id, 0.75f)
        }

        if (variant.hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
            stats.maxSpeed.modifyPercent(id, 10f)

            stats.fluxDissipation.modifyPercent(id, 10f)
            stats.fluxCapacity.modifyPercent(id, 10f)

            stats.ballisticWeaponRangeBonus.modifyPercent(id, 15f)
            stats.energyWeaponRangeBonus.modifyPercent(id, 15f)
            stats.missileWeaponRangeBonus.modifyPercent(id, 15f)

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
        if (fleet.fleetData.membersListCopy.any { it.variant.hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)}) return super.getNPCSpawnWeight(fleet)
        return 0f
    }
}