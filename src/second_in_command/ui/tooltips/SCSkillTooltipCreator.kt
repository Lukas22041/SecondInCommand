package second_in_command.ui.tooltips

import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseAptitudePlugin
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import java.awt.Color

class SCSkillTooltipCreator(var data: SCData, var officer: SCOfficer?, var skill: SCBaseSkillPlugin, var aptitude: SCBaseAptitudePlugin, var requiredSkillPoints: Int, var pickOnlyOne: Boolean, var commonSlotIndex: Int) : BaseTooltipCreator() {


    var sectionMeetsRequirements = true
    var isCommon = skill.id == "sc_common_slot"
    var commonSkillId = officer?.commonSkillSlots?.get(commonSlotIndex)

    override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
        //return skill.spec.modname != "SecondInCommand"
        return false
    }

    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 700f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {

        var aptColor = aptitude.color
        if (isCommon) aptColor = Misc.interpolateColor(Color.WHITE, aptColor, 0.6f)

        var isOrigin = skill.getId() == aptitude.getOriginSkillId()

        if (isCommon && commonSkillId != null && skill.id != commonSkillId) {
            skill = SCSpecStore.getSkillSpec(commonSkillId!!)!!.getPlugin()
        }

        if (isCommon && commonSkillId != null) tooltip!!.addTitle("Skill Slot - ${skill.name}", aptColor)
        else tooltip!!.addTitle(skill.getName(), aptColor)

        if (isCommon) {
            tooltip.addSpacer(10f)
            tooltip.addPara("If acquired, this slot enables selecting a common skill. " +
                    "Common skills are a selection of abilities that every officer has access to, but only one officer can make use of at a time. Press Left-click to select a skill.", 0f,
                Misc.getTextColor(), Misc.getHighlightColor(), "common skill","Left-click")
        }

        if (isCommon && commonSkillId == null) return


        var affectsString = skill.getAffectsString()

        tooltip.addSpacer(10f)
        tooltip.addPara("Affects: $affectsString", 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), affectsString)
        tooltip.addSpacer(10f)

        skill.addTooltip(data, tooltip)

        if (!sectionMeetsRequirements || pickOnlyOne) {
            tooltip.addSpacer(10f)
        }

        if (!sectionMeetsRequirements) {
            var addedS = ""
            if (requiredSkillPoints >= 2) addedS = "s"
            tooltip.addPara("Requires at least $requiredSkillPoints lower tier skill$addedS.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }

        if (pickOnlyOne) {
            tooltip.addPara("You can only pick one skill in this section.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }

        var modname = aptitude.spec.modname
        if (modname != "SecondInCommand" /*&& expanded*/) {
            tooltip.addSpacer(10f)
            //tooltip.addPara("Skill added by \"$modname\"", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
            tooltip.addPara("[$modname]", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "$")
        }

        tooltip.addSpacer(2f)


    }



}