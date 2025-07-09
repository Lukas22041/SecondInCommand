package second_in_command.ui.tooltips

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer
import second_in_command.ui.SCSkillMenuPanel
import second_in_command.ui.elements.OfficerXPBar

class OfficerTooltipCreator(var officer: SCOfficer?, var isAtColony: Boolean, var openedFromPicker: Boolean) : BaseTooltipCreator() {




    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 400f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {

        if (officer == null) {

            tooltip!!.addPara("This slot can be used to assign an Executive Officer. Executive officers provide a selection of skills based on their aptitude. Only the skills from assigned officers are active.",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "Executive Officer", "Only the skills from assigned officers are active")

            tooltip.addSpacer(10f)

            tooltip!!.addPara("Executive Officers can occasionally be hired from a colonies comm-directory. They also sometimes appear in cryo-pods found during exploration.",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "comm-directory", "cryo-pods")

            tooltip.addSpacer(10f)

            addCRWarning(tooltip)

            tooltip.addSpacer(10f)

            tooltip!!.addPara("Left-click to select an executive officer to assign. Right-click to un-assign officers.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "Left-click", "Right-click")





            return
        }

        var plugin = officer!!.getAptitudePlugin()
        var width = getTooltipWidth(null)

        var title = tooltip!!.addTitle(officer!!.person.nameString, plugin.getColor())
        var xPos = width / 2 - title.computeTextWidth(title.text) / 2
        title.position.inTL(xPos, 5f)

        var required = officer!!.getRequiredXP()
        if (officer!!.getCurrentLevel() == officer!!.getMaxLevel()) required = 0f
        var bar = OfficerXPBar(officer!!.getExperiencePoints(), required, plugin.getColor(), tooltip!!, 180f, 25f).apply {
            position.inTMid(25f)
        }


        bar.addText("Lv ${officer!!.getCurrentLevel().toInt()}", Misc.getTextColor())
        bar.centerText()

        var experience = officer!!.getExperiencePoints().toInt()
        var experienceNeeded = officer!!.getRequiredXP().toInt() - experience

        var experienceString = Misc.getWithDGS(experience.toFloat())
        var experienceNeededString = Misc.getWithDGS(experienceNeeded.toFloat())

        var inactiveGain = (SCOfficer.inactiveXPMult * 100).toInt()

        var firstPara = tooltip.addPara("${officer!!.person.nameString} has experience within the ${plugin.getName()} aptitude. All officers have a maximum level of ${officer!!.getMaxLevel()}.", 0f,
        Misc.getTextColor(), Misc.getHighlightColor(), "")
        firstPara.position.inTL(5f, 60f)

        tooltip.addSpacer(10f)

        firstPara.setHighlight(officer!!.person.nameString, plugin.getName(), "${officer!!.getMaxLevel()}")
        firstPara.setHighlightColors(Misc.getHighlightColor(), plugin.getColor(), Misc.getHighlightColor())

        var isAtMax = officer!!.getMaxLevel() == officer!!.getCurrentLevel()

        if (isAtMax) {
            tooltip.addPara("${officer!!.person.heOrShe.capitalize()} has reached ${officer!!.person.hisOrHer} maximum level.", 0f)
        } else {
            tooltip.addPara("${officer!!.person.heOrShe.capitalize()} is currently at level ${officer!!.getCurrentLevel()}. " +
                    "${officer!!.person.heOrShe.capitalize()} has $experienceString experience points and requires $experienceNeededString more to level up.", 0f,
                Misc.getTextColor(), Misc.getHighlightColor(), "${officer!!.getCurrentLevel()}", "$experienceString", "$experienceNeededString")
        }



        tooltip.addSpacer(10f)

        tooltip.addPara("All officers gain experience from battles. Inactive officers also earn experience, but at $inactiveGain%% of the normal rate.", 0f, Misc.getTextColor(), Misc.getHighlightColor(),
            "gain experience from battles" ,"$inactiveGain%")


        if (!SCUtils.isAssociatesBackgroundActive())  {
            tooltip.addSpacer(10f)
            addCRWarning(tooltip)
        }

        /* if (officer!!.getAptitudePlugin().getRequiresDock()) {
             tooltip.addSpacer(10f)
             tooltip.addPara("This officer can only be assigned and un-assigned while the fleet is docked to a colony due to the preparations required for ${officer!!.person.hisOrHer} field of work.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
         }*/


        if (!openedFromPicker) {

            tooltip.addSpacer(10f)
            var extra = ""
            if (Global.getSector().playerPerson.stats.storyPoints <= 3) {
                extra = "You do not have enough story points do so right now."
            }

            var label = tooltip!!.addPara("You can refund all skills by pressing \"R\" while hovering over this officer. This costs 4 story points to do. Can not be done while the skill selection is being edited. $extra", 0f,
                Misc.getTextColor(), Misc.getHighlightColor(), "")

            label.setHighlight("R", "4 story points", extra)
            label.setHighlightColors(Misc.getHighlightColor(), Misc.getStoryOptionColor(), Misc.getNegativeHighlightColor())

            tooltip.addSpacer(10f)


            if (!SCUtils.isAssociatesBackgroundActive()) {
                tooltip!!.addPara("Left-click to select an executive officer to assign. Right-click to un-assign officers.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "Left-click", "Right-click")
            } else {
                tooltip!!.addPara("Due to the background you have chosen, this officer can not be removed or replaced. Instead clicking on their portrait will allow you to change their name and portrait.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "this officer can not be removed or replaced","name", "portrait")
            }


        }

        tooltip.addSpacer(30f)

    }

    fun addCRWarning(tooltip: TooltipMakerAPI) {

        var colonyText = "You are currently in range of a colony."
        var colonyColor = Misc.getPositiveHighlightColor()
        var penalty = (SCSkillMenuPanel.crCost * 100f).toInt()

        if (!isAtColony) {
            colonyText = "You are not in range of a colony."
            colonyColor = Misc.getNegativeHighlightColor()
        }

        var colonyLabel = tooltip.addPara("Replacing or un-assigning an officer while you are not close or docked to a non-hostile colony applies a $penalty%% reduction in combat readiness across the entire fleet. $colonyText",
            0f, Misc.getTextColor(), Misc.getHighlightColor())

        colonyLabel.setHighlight("non-hostile colony", "$penalty%", "combat readiness", colonyText)
        colonyLabel.setHighlightColors(Misc.getHighlightColor(), Misc.getNegativeHighlightColor(), Misc.getHighlightColor(), colonyColor)
    }


}