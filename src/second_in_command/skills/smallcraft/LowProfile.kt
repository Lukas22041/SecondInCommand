package second_in_command.skills.smallcraft

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.specs.SCBaseSkillPlugin

class LowProfile : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    fun getFleetDP(fleet: CampaignFleetAPI) : Float {

        var DP = 0f
        for (member in fleet.fleetData.membersListCopy) {
            if (!member.isFrigate && !member.isDestroyer) continue
            DP += member.deploymentPointsCost
        }
        return DP
    }

    fun getBonus(fleet: CampaignFleetAPI) : Float {
       return 0.3f * getFleetDP(fleet).levelBetween(0f, 240f)
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        var DP = getFleetDP(data.fleet).toInt()
        var bonus = (getBonus(data.fleet) * 100).toInt()

        tooltip.addPara("Reduces the sensor profile of the fleet based on how many frigates and destroyers are in the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The increase is between 0%%-30%% based on the amount of of frigates and destroyers in the fleet",0f, Misc.getTextColor(), Misc.getHighlightColor(), "0%","30%")
        tooltip.addPara("   - It reaches its maximum when there are 240 deployment points worth of frigates and destroyers available",0f, Misc.getTextColor(), Misc.getHighlightColor(), "240 deployment points")
        tooltip.addPara("   - The fleet total is currently at $DP points, providing a $bonus%% bonus",0f, Misc.getTextColor(), Misc.getHighlightColor(), "$DP", "$bonus%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.detectedRangeMod.modifyMult("sc_low_profile", 1f-getBonus(data.fleet), "Low profile")
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun onDeactivation(data: SCData) {
       data.fleet.stats.detectedRangeMod.unmodify("sc_low_profile")
    }

}