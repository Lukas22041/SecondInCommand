package second_in_command.ui.tooltips

import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCOfficer
import second_in_command.ui.elements.OfficerXPBar

class OfficerTooltipCreator(var officer: SCOfficer?) : BaseTooltipCreator() {




    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 350f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {

        if (officer == null) {

            tooltip!!.addPara("Left-click on an empty portrait to select an executive officer to assign. Right-click to un-assign officers.",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "Left-click", "Right-click")

            return
        }

        var plugin = officer!!.getAptitudePlugin()
        var width = getTooltipWidth(null)

        var title = tooltip!!.addTitle(officer!!.person.nameString, plugin.getColor())
        var xPos = width / 2 - title.computeTextWidth(title.text) / 2
        title.position.inTL(xPos, 5f)

        var bar = OfficerXPBar(officer!!.getExperiencePoints(), officer!!.getRequiredXP(), plugin.getColor(), tooltip!!, 180f, 25f).apply {
            position.inTMid(25f)
        }


        bar.addText("Lv ${officer!!.getCurrentLevel().toInt()}", Misc.getTextColor())
        bar.centerText()

        var experience = officer!!.getExperiencePoints().toInt()
        var experienceNeeded = officer!!.getRequiredXP().toInt() - experience

        var experienceString = Misc.getWithDGS(experience.toFloat())
        var experienceNeededString = Misc.getWithDGS(experienceNeeded.toFloat())

        var inactiveGain = (SCOfficer.inactiveXPMult * 100).toInt()

        var firstPara = tooltip.addPara("${officer!!.person.nameString} has experience within the ${plugin.getName()} aptitude. This aptitude has a maximum level of ${officer!!.getMaxLevel()}.", 0f,
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


        if (officer!!.getAptitudePlugin().getRequiresDock()) {
            tooltip.addSpacer(10f)
            tooltip.addPara("This officer can only be assigned and un-assigned while the fleet is docked to a colony due to the preparations required for ${officer!!.person.hisOrHer} field of work.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }

        tooltip.addSpacer(30f)

    }



}