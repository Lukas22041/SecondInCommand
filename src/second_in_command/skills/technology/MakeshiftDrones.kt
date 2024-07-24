package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.skills.automated.AutomatedShips
import second_in_command.specs.SCBaseSkillPlugin

class MakeshiftDrones : SCBaseSkillPlugin() {

    fun isAutomatedActive() = SCUtils.isSkillActive("sc_automated_automated_ships")

    companion object {
        var BASE_POINTS = 60f
    }

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

       /* tooltip.addPara("This is a lesser version of the \"Automated Ships\" skill", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This version only provides ${BASE_POINTS.toInt()} automated points", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${BASE_POINTS.toInt()}")
        tooltip.addPara("   - Those points are added on to the main version if both are active", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The main version can be found in the \"Automation\" aptitude", 0f, Misc.getTextColor(), Misc.getHighlightColor())*/

     /*   tooltip.addSpacer(10f)
        tooltip.addSectionHeading("Automated Ships", Alignment.MID, 0f)
        tooltip.addSpacer(10f)*/

        AutomatedShips.createTooltip(tooltip, BASE_POINTS, true)
    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (!isAutomatedActive()) {
            AutomatedShips.applyEffects(getName(), stats, variant, hullSize, id)
        }


    }

    override fun advance(amount: Float) {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
    }

    override fun onActivation() {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
    }

    override fun onDeactivation() {
        if (!AutomatedShips.isAnyAutoSkillActive()) {
            Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE)
        }
    }
}