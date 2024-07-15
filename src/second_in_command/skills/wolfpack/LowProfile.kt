package second_in_command.skills.wolfpack

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.levelBetween
import second_in_command.specs.SCBaseSkillPlugin

class LowProfile : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    fun getFleetDP() : Float {
        var fleet = Global.getSector().playerFleet

        var DP = 0f
        for (member in fleet.fleetData.membersListCopy) {
            if (!member.isFrigate && !member.isDestroyer) continue
            DP += member.deploymentPointsCost
        }
        return DP
    }

    fun getBonus() : Float {
       return 0.3f * getFleetDP().levelBetween(0f, 120f)
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        var DP = getFleetDP().toInt()
        var bonus = (getBonus() * 100).toInt()

        tooltip.addPara("Reduces the sensor profile of the fleet based on how many frigates and destroyers are in the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The increase is between 0%%-30%% based on the amount of of frigates and destroyers in the fleet",0f, Misc.getTextColor(), Misc.getHighlightColor(), "0%","30%")
        tooltip.addPara("   - It reaches its maximum when there are 120 deployment points worth of frigates and destroyers available",0f, Misc.getTextColor(), Misc.getHighlightColor(), "120 deployment points")
        tooltip.addPara("   - The fleet total is currently at $DP points, providing a $bonus%% bonus",0f, Misc.getTextColor(), Misc.getHighlightColor(), "$DP", "$bonus%")

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun advance(amount: Float) {
        Global.getSector().playerFleet.stats.detectedRangeMod.modifyMult("sc_low_profile", 1f-getBonus(), "Low profile")
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun onDeactivation() {
        Global.getSector().playerFleet.stats.detectedRangeMod.unmodify("sc_low_profile")

    }

}