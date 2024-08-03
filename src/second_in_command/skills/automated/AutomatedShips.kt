package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.CampaignFleetAPI
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
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.skills.technology.MakeshiftDrones
import second_in_command.specs.SCBaseSkillPlugin
import kotlin.math.roundToInt

class AutomatedShips : SCBaseSkillPlugin() {

    companion object {

        var MAX_CR_BONUS = 100f
        var BASE_POINTS = 120f

        fun getMaximumPoints(fleet: CampaignFleetAPI) : Float {
            var points = 0f
            if (SCUtils.getFleetData(fleet).isSkillActive("sc_automated_automated_ships")) points += BASE_POINTS
            if (SCUtils.getFleetData(fleet).isSkillActive("sc_technology_makeshift_drones")) points += MakeshiftDrones.BASE_POINTS
            if (SCUtils.getFleetData(fleet).isSkillActive("sc_automated_specialised_equipment")) points += SpecialisedEquipment.points
            if (SCUtils.getFleetData(fleet).isSkillActive("sc_automated_expertise")) points += AutonomousExpertise.points
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

        fun createTooltip(data: SCData, tooltip: TooltipMakerAPI, dp: Float, makeshift: Boolean) {

            if (data.isNPC) {

                tooltip.addPara("This fleet is capable of fielding automated ships", 0f)

                return
            }

            val alpha = AICoreOfficerPluginImpl.ALPHA_MULT.roundToInt()
            val beta = AICoreOfficerPluginImpl.BETA_MULT.roundToInt()
            val gamma = AICoreOfficerPluginImpl.GAMMA_MULT.roundToInt()

            var automatedDP = 0f
            if (Global.getSector()?.playerFleet?.fleetData != null) {
                automatedDP = getAutomatedPoints(Global.getSector().playerFleet.fleetData)
            }

            var maxPoints = getMaximumPoints(data.fleet)
            var bonus = SCUtils.computeThresholdBonus(automatedDP, MAX_CR_BONUS, maxPoints)

            tooltip.addPara("Enables the recovery of some automated ships, such as derelict drones", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
            tooltip.addPara("Automated ships can only be captained by AI cores", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

            tooltip.addSpacer(10f)

            tooltip.addPara("+${bonus.toInt()}%% combat readiness (maximum ${MAX_CR_BONUS.toInt()}%%); offsets built-in 100%% penalty", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())


            if (!isAnyAutoSkillActive(data.fleet)) {
                maxPoints = dp
            }

            tooltip.addPara("   - Maximum at ${maxPoints.toInt()} or less total automated ship points*, your fleet total is ${automatedDP.toInt()}", 0f,
                Misc.getTextColor(), Misc.getHighlightColor(), "${maxPoints.toInt()}", "${automatedDP.toInt()}")

            if (!makeshift) {
                tooltip.addPara("   - Other skills within this aptitude can increase the amount of available automated ship points", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "")
            }

            tooltip.addPara("   - This skill contributes a baseline of ${dp.toInt()} points to the limit", 0f,
                Misc.getTextColor(), Misc.getHighlightColor(), "${dp.toInt()}")



            tooltip.addSpacer(10f)

            if (makeshift) {
                tooltip.addPara("This is a weaker version of the \"Automated Ships\" skill from the \"Automation\" aptitude", 0f,
                    Misc.getHighlightColor(), Misc.getHighlightColor(), "Automated Ships", "Automation")
                tooltip.addPara("   - If both are active, their contributed automated points are combined", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "Automated Ships", "Automation")

                tooltip.addSpacer(10f)
            }

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


        fun applyEffects(data: SCData, skill: String, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
            if (Misc.isAutomated(stats) && !Automated.isAutomatedNoPenalty(stats)) {

                var automatedDP = 0f
                if (Global.getSector()?.playerFleet?.fleetData != null) {
                    automatedDP = getAutomatedPoints(Global.getSector().playerFleet.fleetData)
                }

                var maxPoints = getMaximumPoints(data.fleet)
                var bonus = SCUtils.computeThresholdBonus(automatedDP, MAX_CR_BONUS, maxPoints)

                stats!!.maxCombatReadiness.modifyFlat(id, bonus * 0.01f, "${skill} skill")
            }
        }

        fun isAnyAutoSkillActive(fleet: CampaignFleetAPI) : Boolean {

            var auto = SCUtils.getFleetData(fleet).isSkillActive("sc_automated_automated_ships")
            var makeshift = SCUtils.getFleetData(fleet).isSkillActive("sc_technology_makeshift_drones")

            return auto || makeshift
        }

    }

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        AutomatedShips.createTooltip(data, tooltip, BASE_POINTS, false)

    }


    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?,variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (data.isPlayer) {
            applyEffects(data, getName(), stats, variant, hullSize, id)
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

        if (!isAnyAutoSkillActive(data.fleet) && !data.isNPC) {
            Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE)
        }

    }




}