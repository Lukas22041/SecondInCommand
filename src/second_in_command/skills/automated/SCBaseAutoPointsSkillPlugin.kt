package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.AICoreOfficerPluginImpl
import com.fs.starfarer.api.impl.campaign.ids.Strings
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.scripts.AutomatedShipsManager
import second_in_command.specs.SCBaseSkillPlugin
import kotlin.math.roundToInt

abstract class SCBaseAutoPointsSkillPlugin : SCBaseSkillPlugin() {

    abstract fun getProvidedPoints() : Int

    override fun getAffectsString(): String {
        return "all automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        if (data.isNPC) {
            tooltip.addPara("Enables the fleet to field automated ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
            tooltip.addPara("Automated ships can only be captained by AI cores", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
            return
        }

        var manager = AutomatedShipsManager.get()
        var provided = getProvidedPoints()
        var maximum = manager.getMaximumDP()
        var used = manager.getUsedDP()
        tooltip.addPara("+$provided automated ship points", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("Having more than zero automated ship points enables the recovery of automated ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Automated ships can only be captained by AI cores", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        if (!data.isSkillActive(spec.id)) {
            maximum += provided
        }

        var bonus = SCUtils.computeThresholdBonus(used, manager.MAX_CR_BONUS, maximum)

        tooltip.addPara("+${bonus.toInt()}%% combat readiness (maximum ${manager.MAX_CR_BONUS.toInt()}%%) - shared across similar skills - offsets built-in 100%% penalty", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addPara("   - At maximum effectiveness while less than your total budget of automated ship points is used", 0f,
            Misc.getTextColor(), Misc.getHighlightColor(), )

        tooltip.addPara("   - Your maximum budget, including the amount provided by this skill, is at ${maximum.toInt()} automated ship points", 0f,
            Misc.getTextColor(), Misc.getHighlightColor(), "${maximum.toInt()}")

        tooltip.addPara("   - Your fleet currently uses ${used.toInt()} automated ship points", 0f,
            Misc.getTextColor(), Misc.getHighlightColor(), "${used.toInt()}")

        tooltip.addSpacer(10f)

        val alpha = AICoreOfficerPluginImpl.ALPHA_MULT.roundToInt()
        val beta = AICoreOfficerPluginImpl.BETA_MULT.roundToInt()
        val gamma = AICoreOfficerPluginImpl.GAMMA_MULT.roundToInt()
        var label = tooltip.addPara("" +
                "The total \"automated ship points\" are equal to the deployment points cost of " +
                "all automated ships in the fleet, with a multiplier for installed AI cores - " +
                "${alpha}${Strings.X} for an Alpha Core, " +
                "${beta}${Strings.X} for an Beta Core, " +
                "${gamma}${Strings.X} for a Gamma Core. " +
                "Due to safety interlocks, ships with AI cores do not contribute to the deployment point distribution.", 0f,
            Misc.getGrayColor(), Misc.getHighlightColor())

        label.setHighlight("${alpha}${Strings.X}", "${beta}${Strings.X}", "${gamma}${Strings.X}", "do not contribute to the deployment point distribution")
        label.setHighlightColors(Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getNegativeHighlightColor())
    }

    override fun advance(data: SCData, amunt: Float?) {
        if (data.isNPC) return
        Global.getSector().playerPerson.stats.dynamic.getMod("sc_auto_dp").modifyFlat("${spec.id}", getProvidedPoints().toFloat(), spec.name)
    }

    override fun onDeactivation(data: SCData) {
        if (data.isNPC) return
        Global.getSector().playerPerson.stats.dynamic.getMod("sc_auto_dp").unmodify("${spec.id}")
    }

}