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

class Redistribution : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships with the \"Shield Shunt\" hullmod gain the following effects: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ships flux dissipation is increased by 5%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")
        tooltip.addPara("   - The ships armor is increased by an additional 10%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - The ships receives 20%% less emp damage", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (variant.hasHullMod(HullMods.SHIELD_SHUNT)) {


            stats.fluxDissipation.modifyPercent(id, 5f)
            stats.armorBonus.modifyPercent(id, 10f)
            stats.empDamageTakenMult.modifyMult(id, 0.75f)



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
        if (fleet.fleetData.membersListCopy.any { it.variant.hasHullMod(HullMods.SHIELD_SHUNT)}) return super.getNPCSpawnWeight(fleet)
        return 0f
    }
}