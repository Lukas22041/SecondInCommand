package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class WingTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "carriers up to 90 DP"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: +50% fighter replace rate, +20% engagement range, +25 effective armor to fighters
        //       (scales per fighter bay), 90 DP limit
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        // TODO: implement
    }
}
