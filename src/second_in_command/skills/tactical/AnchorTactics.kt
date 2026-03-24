package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class AnchorTactics : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "capitals and nearby allied ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        // TODO: +20% range, +10 max speed and maneuverability,
        //       +10% range to nearby allied ships (does not stack with other capitals,
        //       does not affect other capitals), 90 DP threshold
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        // TODO: implement
    }
}
