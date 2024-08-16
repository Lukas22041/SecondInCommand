package second_in_command.skills

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import java.awt.Color

//Class for Handling Effects activated on certain levels
object PlayerLevelEffects {


    fun getTooltipForLevel(tooltip: TooltipMakerAPI, level: Int) {
        when (level) {
            2 -> tooltip.addPara("+10% Experience gain for Executive Officers", 0f, getColor(level), getColor(level))
            else -> if (level % 2 == 0) tooltip.addPara("Gain 1 skill point", 0f, getColor(level), getColor(level))

        }
    }

    fun getIconForLevel(level: Int) : String {
        return when (level) {
            else -> {
                if (level % 2 == 0) ""
                else ""
            }
        }
    }

    fun getColor(level: Int) : Color {
        if (Global.getSector().playerPerson.stats.level >= level) {
            return Misc.getHighlightColor()
        }
        return Misc.getGrayColor()
    }



    fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: HullSize, id: String) {

    }

    fun applyEffectsToFighterSpawnedByShip(data: SCData?, fighter: ShipAPI?, ship: ShipAPI?, id: String) {}

    /**Ship specific campaign advance */
    fun advanceInCampaign(data: SCData, member: FleetMemberAPI, amount: Float) {

    }

    /**Non-ship specific campaign advance */
    fun advance(data: SCData, amunt: Float) {}

    /**Ship specific combat advance */
    fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

    }

}