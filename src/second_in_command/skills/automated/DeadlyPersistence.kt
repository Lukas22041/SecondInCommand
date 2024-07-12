package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class DeadlyPersistence : SCBaseSkillPlugin() {


    var ZERO_FLUX_MIN = 0.1f
    var VENT_RATE = 20f
    var COOLDOWN_REDUCTION = 0.9f


    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("The zero-flux-boost can trigger as long as the ship is below 10%% of its flux capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+20%% to the ships active vent rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        //tooltip.addPara("-10%% on the cooldown of the ships system", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {
            stats!!.zeroFluxMinimumFluxLevel.modifyFlat(id, ZERO_FLUX_MIN)
            stats.ventRateMult.modifyPercent(id, VENT_RATE)
            //stats.getSystemCooldownBonus().modifyMult(id, COOLDOWN_REDUCTION)
        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}