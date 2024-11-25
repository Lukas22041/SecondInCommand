package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class DeadlyPersistence : SCBaseSkillPlugin() {


    var ZERO_FLUX_MIN = 0.1f
    var VENT_RATE = 20f
    var COOLDOWN_REDUCTION = 0.9f


    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The zero-flux-boost can trigger as long as the ship is below 10%% of its flux capacity*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+20%% to the ships active vent rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        //tooltip.addPara("-10%% on the cooldown of the ships system", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addSpacer(10f)
        tooltip.addPara("*The effect can stack with others of the same kind, two of them would add up to a minimum of 20%%.", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "20%")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {
            stats!!.zeroFluxMinimumFluxLevel.modifyFlat(id, ZERO_FLUX_MIN)
            stats.ventRateMult.modifyPercent(id, VENT_RATE)
            //stats.getSystemCooldownBonus().modifyMult(id, COOLDOWN_REDUCTION)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}