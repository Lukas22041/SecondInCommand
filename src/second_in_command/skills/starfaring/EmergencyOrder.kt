package second_in_command.skills.starfaring

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class EmergencyOrder : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The \"Transverse Jump\" and \"Emergency Burn\" ability no longer reduce the fleets combat readiness", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-20%% damage from hazards such as hyperspace storms and solar flares", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.dynamic.getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 0.7f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getStat(Stats.EMERGENCY_BURN_CR_MULT).modifyMult("sc_emergency_order", 0f)
        data.fleet.stats.dynamic.getStat(Stats.DIRECT_JUMP_CR_MULT).modifyMult("sc_emergency_order", 0f)
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.EMERGENCY_BURN_CR_MULT).modifyMult("sc_emergency_order", 0f)
        data.fleet.stats.dynamic.getStat(Stats.DIRECT_JUMP_CR_MULT).modifyMult("sc_emergency_order", 0f)
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.EMERGENCY_BURN_CR_MULT).unmodify("sc_emergency_order")
        data.fleet.stats.dynamic.getStat(Stats.DIRECT_JUMP_CR_MULT).unmodify("sc_emergency_order")
    }

}