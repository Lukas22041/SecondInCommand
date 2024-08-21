package second_in_command.skills

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import java.awt.Color

//Class for Handling Effects activated on certain levels
object PlayerLevelEffects {


    fun getTooltipForLevel(level: Int) : String{
        return when (level) {
            2 -> "+10%% experience gain for executive officers"
            4 -> "+10%% experience gain for executive officers"
            6 -> "+10%% experience gain for executive officers"
            8 -> "+20%% experience gain for executive officers"
            10 -> "+2 maximum number of officers you're able to command"
            12 -> "+1 maximum level for officers under your command"
            14 -> "+1 maximum elite skills for officers under your command"
            else -> ""
        }
    }

    fun getIconForLevel(level: Int) : String {
        return when (level) {
            else ->  ""
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
    fun advance(data: SCData, amunt: Float) {

        var player = Global.getSector().playerPerson
        var playerLevel = player.stats.level

        if (playerLevel >= 10) {
            data.commander.stats.officerNumber.modifyFlat("sc_level_up_effect", 2f)
        }

        if (playerLevel >= 12) {
            data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("sc_level_up_effect", 1f)
        }

        if (playerLevel >= 14) {
            data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_ELITE_SKILLS_MOD).modifyFlat("sc_level_up_effect", 1f)
        }

    }

    /**Ship specific combat advance */
    fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

    }

}