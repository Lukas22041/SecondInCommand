package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SustainTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "ships up to 120 DP"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: +20% fuel and supply reduction, 120 DP limit
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        // TODO: implement
    }
}
