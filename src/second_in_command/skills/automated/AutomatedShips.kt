package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.AICoreOfficerPluginImpl
import com.fs.starfarer.api.impl.campaign.ids.Strings
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.specs.SCBaseSkillPlugin
import kotlin.math.roundToInt

class AutomatedShips : SCBaseSkillPlugin() {

    var MAX_CR_BONUS = 100f
    var BASE_POINTS = 120f



    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        val alpha = AICoreOfficerPluginImpl.ALPHA_MULT.roundToInt()
        val beta = AICoreOfficerPluginImpl.BETA_MULT.roundToInt()
        val gamma = AICoreOfficerPluginImpl.GAMMA_MULT.roundToInt()

        var automatedDP = getAutomatedPoints(Global.getSector().playerFleet.fleetData)
        var maxPoints = getMaximumPoints()
        var bonus = SCUtils.computeThresholdBonus(automatedDP, MAX_CR_BONUS, maxPoints)

        tooltip.addPara("Enables the recovery of some automated ships, such as derelict drones", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Automated ships can only be captained by AI cores", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("+${bonus.toInt()}%% combat readiness (maximum ${MAX_CR_BONUS.toInt()}%%); offsets built-in 100%% penalty", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addPara("   - Maximum at ${maxPoints.toInt()} or less total automated ship points*, your fleet total is ${automatedDP.toInt()}", 0f,
        Misc.getTextColor(), Misc.getHighlightColor(), "${maxPoints.toInt()}", "${automatedDP.toInt()}")

        tooltip.addPara("   - Other skills within this aptitude can increase the amount of available automated ship points", 0f,
            Misc.getTextColor(), Misc.getHighlightColor(), "")

        tooltip.addPara("   - This skill contributes a baseline of ${BASE_POINTS.toInt()} points to the limit", 0f,
            Misc.getTextColor(), Misc.getHighlightColor(), "${BASE_POINTS.toInt()}")

        tooltip.addSpacer(10f)

        var label = tooltip.addPara("" +
                "*The total \"automated ship points\" are equal to the deployment points cost of " +
                "all automated ships in the fleet, with a multiplier for installed AI cores - " +
                "${alpha}${Strings.X} for an Alpha Core, " +
                "${beta}${Strings.X} for an Beta Core, " +
                "${gamma}${Strings.X} for a Gamma Core. " +
                "Due to safety interlocks, ships with AI cores do not contribute to the deployment point distribution.", 0f,
        Misc.getGrayColor(), Misc.getHighlightColor())

        label.setHighlight("${alpha}${Strings.X}", "${beta}${Strings.X}", "${gamma}${Strings.X}", "do not contribute to the deployment point distribution")
        label.setHighlightColors(Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getNegativeHighlightColor())
    }


    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (Misc.isAutomated(stats) && !Automated.isAutomatedNoPenalty(stats)) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)

            var automatedDP = getAutomatedPoints(Global.getSector().playerFleet.fleetData)
            var maxPoints = getMaximumPoints()
            var bonus = SCUtils.computeThresholdBonus(automatedDP, MAX_CR_BONUS, maxPoints)

            stats!!.maxCombatReadiness.modifyFlat(id, bonus * 0.01f, "${getName()} skill")
        }
    }

    override fun advance(amount: Float) {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
    }

    override fun onActivation() {
        Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
    }

    override fun onDeactivation() {
        Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE)
    }

    fun getMaximumPoints() : Float {
        var points = 0f
        points += BASE_POINTS
        if (SCUtils.isSkillActive("sc_automated_specialised_equipment")) points += SpecialisedEquipment.points
        if (SCUtils.isSkillActive("sc_automated_expertise")) points += AutonomousExpertise.points
        return points
    }

    fun getAutomatedPoints(data: FleetDataAPI): Float {
        var points = 0f
        for (curr in data.membersListCopy) {
            if (curr.isMothballed) continue
            if (!Misc.isAutomated(curr)) continue
            if (Automated.isAutomatedNoPenalty(curr)) continue
            var mult = 1f
            //if (curr.getCaptain().isAICore()) {
            points += curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE)
            mult = curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT)
            if (mult == 0f) mult = 1f
            //}
            points += Math.round(curr.deploymentPointsCost * mult).toFloat()
        }
        return Math.round(points).toFloat()
    }


}