package second_in_command.skills.engineering

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FuelOptimisation : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+1 maximum burn level", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-20%% fuel usage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.fuelUseMod.modifyMult(id, 0.80f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_fuel_optimisation", 1f, "Fuel Optimisation Skill")
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_fuel_optimisation", 1f, "Fuel Optimisation Skill")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.fleetwideMaxBurnMod.unmodify("sc_fuel_optimisation")
    }

}