package second_in_command.skills.tactical

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCBaseSkillPlugin

class Spotters : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates in the fleet"
    }

    fun getFleetDP(fleet: CampaignFleetAPI) : Float {
        if (Global.getCurrentState() == GameState.TITLE) return 0f
        var DP = 0f
        for (member in fleet.fleetData.membersListCopy) {
            if (!member.isFrigate && !member.isDestroyer) continue
            DP += member.deploymentPointsCost
        }
        return DP
    }

    fun getBonus(fleet: CampaignFleetAPI) : Float {
        return 0.2f * getFleetDP(fleet).levelBetween(0f, 120f)
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All frigates gain an additional 1000 units of in-combat vision range", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This makes them able to reveal opponents from further distances",0f, Misc.getTextColor(), Misc.getHighlightColor(), "")

        tooltip.addSpacer(10f)

        var DP = getFleetDP(data.fleet).toInt()
        var bonus = (getBonus(data.fleet) * 100).toInt()

        tooltip.addPara("Increases the sensor range of the fleet based on how many frigates there are in the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The increase is between 0%%-20%% based on the amount of of frigates in the fleet",0f, Misc.getTextColor(), Misc.getHighlightColor(), "0%","20%")
        tooltip.addPara("   - It reaches its maximum when there are 120 deployment points worth of frigates available",0f, Misc.getTextColor(), Misc.getHighlightColor(), "120 deployment points")
        tooltip.addPara("   - The fleet total is currently at $DP points, providing a $bonus%% bonus",0f, Misc.getTextColor(), Misc.getHighlightColor(), "$DP", "$bonus%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (hullSize == ShipAPI.HullSize.FRIGATE) {
            stats!!.sightRadiusMod.modifyFlat(id, 1000f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.sensorRangeMod.modifyMult("sc_spotters", 1f+getBonus(data.fleet), "Spotters")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.sensorRangeMod.unmodify("sc_spotters")
    }
}