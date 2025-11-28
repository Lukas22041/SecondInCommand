package second_in_command.skills.scavenging

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.specs.SCBaseSkillPlugin

class SafeTransport : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-30%% damage from hazards such as hyperspace storms and solar flares", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Ships that are mothballed gain the following benefits", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The fuel consumption is reduced by 75%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "75%")
        tooltip.addPara("   - The ships burn level is increased by 1", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "1")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {
        stats.dynamic.getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 0.70f)

        var member = stats.fleetMember ?: return
        if (member.isMothballed) {
            stats.fuelUseMod.modifyMult(id, 0.25f)
            stats.maxBurnLevel.modifyFlat(id, 1f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}