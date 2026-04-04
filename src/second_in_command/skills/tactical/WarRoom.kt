package second_in_command.skills.tactical

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.skills.management.CrewTraining
import second_in_command.specs.SCBaseSkillPlugin

class WarRoom : SCBaseSkillPlugin() {

    val NUM_OFFICER_BONUS = 2f

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        val baseOfficers = Global.getSector().getPlayerStats().getOfficerNumber().getBaseValue().toInt()

        tooltip.addPara("Tactical is build around \"Tactics\"\n" +
                "Tactics are skills which come with strong effects, but diminishing returns past their thresholds.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("+ ${NUM_OFFICER_BONUS.toInt()} to maximum number of ship officers* you're able to command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this executive officer is unassigned, any officer over the limit will also be unassigned", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")

        tooltip.addSpacer(10f)

        tooltip.addPara("+1 command points in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("30%% faster command point recovery in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara( "*The base maximum number of officers you're able to command is " + baseOfficers + ".", 0f,Misc.getGrayColor(), Misc.getHighlightColor())
    }

    override fun onActivation(data: SCData) {
        data.commander.stats.officerNumber.modifyFlat(id, NUM_OFFICER_BONUS)
        data.commander.stats.commandPoints.modifyFlat(id, 1f)
        data.commander.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).modifyFlat(id, 0.3f)
    }

    override fun advance(data: SCData, amunt: Float?) {
        data.commander.stats.officerNumber.modifyFlat(id, NUM_OFFICER_BONUS)
        data.commander.stats.commandPoints.modifyFlat(id, 1f)
        data.commander.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).modifyFlat(id, 0.3f)
    }

    override fun onDeactivation(data: SCData) {
        data.commander.stats.officerNumber.unmodify(id)
        data.commander.stats.commandPoints.unmodify(id)
        data.commander.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).unmodify(id)

        if (!data.isNPC) {
            CrewTraining.removeOfficersOverTheLimit()
        }
    }
}
