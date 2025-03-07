package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Strings
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class NeuralJunction : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "flagship"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The player can be assigned to pilot automated ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The player counts as a 1${Strings.X} multiplier for automated points", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "1${Strings.X}")
        tooltip.addPara("   - Automated ships piloted by the player have 15%% more maximum combat readiness", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - If this officer is unassigned, the player is moved to another ship", 0f, Misc.getTextColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData?, stats: MutableShipStatsAPI?, variant: ShipVariantAPI?, hullSize: ShipAPI.HullSize?, id: String?) {

        var captain = stats!!.fleetMember?.captain
        if (stats.isAutomated() && captain != null && captain == Global.getSector().playerPerson) {
            stats!!.maxCombatReadiness.modifyFlat("sc_crew_training", 0.15f, "Neural Junction")
        }


    }

    override fun onDeactivation(data: SCData) {
        var playership = Global.getSector().playerFleet.fleetData.membersListCopy.find { it.captain?.isPlayer == true } ?: return

        if (Misc.isAutomated(playership)) {
            var members = Global.getSector().playerFleet.fleetData.membersListCopy
            var nonAutomatedShip = members.find { !Misc.isAutomated(it) && (it.captain == null || it.captain.isDefault) }
            if (nonAutomatedShip == null) {
                nonAutomatedShip = members.find { !Misc.isAutomated(it)}
            }

            if (nonAutomatedShip != null) {
                nonAutomatedShip.captain = Global.getSector().playerPerson
                playership.captain = null
            }
        }
    }

}