package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.skills.tactical.scripts.StrikeTacticsScript
import second_in_command.specs.SCBaseSkillPlugin

class StrikeTactics : SCBaseSkillPlugin() {

    var thresholdLimit = 90
    var baseRegenTime = 60 //Seconds, increases if you are over the threshold, 60s at threshold or above, 120s at twice over threshold, and so forth

    override fun getAffectsString(): String {
        return "all combat ships, including carriers and militarized civilian ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        var points = SCThresholds.getMissileWeaponPoints(data.fleet.fleetData)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (BaseSkillEffectDescription.isCivilian(stats)) {

        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI?, id: String) {
        if (BaseSkillEffectDescription.isCivilian(ship.mutableStats)) {
            var regenTime = 0f
            ship.addListener(StrikeTacticsScript(ship, regenTime))
        }
    }
}
