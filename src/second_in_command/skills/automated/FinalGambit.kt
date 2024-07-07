package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.magiclib.subsystems.MagicSubsystemsManager
import second_in_command.skills.automated.scripts.FinalGambitActivator
import second_in_command.specs.SCBaseSkillPlugin

class FinalGambit : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("Provides the ship with the \"Final Gambit\" subsystem.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Activating the subsystem toggles the burn drive and enables a strong damper field", 0f)
        tooltip.addPara("   - When the duration of the subsystem ends, the ship explodes with much more force and range than usual", 0f)
        tooltip.addPara("   - The AI toggles the system below 50%% hitpoints if targets are nearby", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "50%")

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {

        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {
            var activator = FinalGambitActivator(ship)
            MagicSubsystemsManager.addSubsystemToShip(ship!!, activator)
        }
    }


}