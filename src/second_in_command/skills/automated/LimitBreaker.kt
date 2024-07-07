package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class LimitBreaker : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {


    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {

        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}