package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.skills.automated.AutomatedShips
import second_in_command.specs.SCBaseSkillPlugin

class MakeshiftDrones : SCBaseSkillPlugin() {

    fun isAutomatedActive(fleet: CampaignFleetAPI) = SCUtils.getFleetData(fleet).isSkillActive("sc_automated_automated_ships")

    companion object {
        var BASE_POINTS = 60f
    }

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

       /* tooltip.addPara("This is a lesser version of the \"Automated Ships\" skill", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This version only provides ${BASE_POINTS.toInt()} automated points", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${BASE_POINTS.toInt()}")
        tooltip.addPara("   - Those points are added on to the main version if both are active", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The main version can be found in the \"Automation\" aptitude", 0f, Misc.getTextColor(), Misc.getHighlightColor())*/

     /*   tooltip.addSpacer(10f)
        tooltip.addSectionHeading("Automated Ships", Alignment.MID, 0f)
        tooltip.addSpacer(10f)*/

        AutomatedShips.createTooltip(data, tooltip, BASE_POINTS, true)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (!isAutomatedActive(data.fleet)) {
            AutomatedShips.applyEffects(data, getName(), stats, variant, hullSize, id)
        }


    }

    override fun advance(data: SCData, amount: Float) {
        if (!data.isNPC) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
        }
    }

    override fun onActivation(data: SCData) {
        if (!data.isNPC) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
        }
    }

    override fun onDeactivation(data: SCData) {
        if (!AutomatedShips.isAnyAutoSkillActive(data.fleet) && !data.isNPC) {
            Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE)
        }
    }
}