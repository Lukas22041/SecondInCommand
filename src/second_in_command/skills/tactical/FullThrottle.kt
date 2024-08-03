package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FullThrottle : SCBaseSkillPlugin() {

    var ZERO_FLUX_MIN = 0.1f
    var ZERO_FLUX_BOOST = 20f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The zero-flux-boost can trigger as long as the ship is below 10%% of its flux capacity*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor(), )
        tooltip.addPara("+20 speed while the zero-flux-boost is active ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("*The effect can stack with others of the same kind, two of them would add up to a minimum of 20%%", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "20%")
        
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.zeroFluxMinimumFluxLevel.modifyFlat(id, ZERO_FLUX_MIN)
        stats!!.zeroFluxSpeedBoost.modifyFlat(id, ZERO_FLUX_BOOST)


    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}