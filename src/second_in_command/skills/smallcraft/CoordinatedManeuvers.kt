package second_in_command.skills.smallcraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.CoordinatedManeuversScript
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class CoordinatedManeuvers : SCBaseSkillPlugin() {

    var max = CoordinatedManeuversScript.BASE_MAXIMUM.toInt()

    override fun getAffectsString(): String {
        return "all ships with officers, including flagship"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Increased nav rating* of fleet for deployed ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 6%% per deployed frigate",0f, Misc.getTextColor(), Misc.getHighlightColor(), "6%")
        tooltip.addPara("   - 3%% per deployed destroyer",0f, Misc.getTextColor(), Misc.getHighlightColor(), "3%")
        tooltip.addPara("   - 1%% per larger hull",0f, Misc.getTextColor(), Misc.getHighlightColor(), "1%")

        tooltip.addSpacer(10f)

        tooltip.addPara("+50%% to command point recovery rate from deployed frigates, +25%% from destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)


        tooltip.addPara("*The total nav rating for the deployed ships of the fleet increases the top speed of all ships " +
                "in the fleet, up to a maximum of $max%%. Does not apply to fighters.", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "$max%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var officer = stats?.fleetMember?.captain ?: return
        if (officer.isDefault) return

        var navBonus = 0f
        navBonus = when (hullSize) {
            ShipAPI.HullSize.FRIGATE -> 6f
            ShipAPI.HullSize.DESTROYER -> 3f
            else -> 1f
        }

        stats.dynamic.getMod(Stats.COORDINATED_MANEUVERS_FLAT).modifyFlat(id, navBonus)

        var commandBonus = 0f

        commandBonus = when (hullSize) {
            ShipAPI.HullSize.FRIGATE -> 50f
            ShipAPI.HullSize.DESTROYER -> 25f
            else -> 0f
        }

        stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).modifyFlat(id, commandBonus * 0.01f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}