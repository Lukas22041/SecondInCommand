package second_in_command.interactions.rules

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import org.magiclib.kotlin.isAutomated
import second_in_command.SCUtils
import second_in_command.misc.NPCOfficerGenerator
import second_in_command.misc.SCSettings
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCAddOfficersToFleetInteraction : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {

        var fleet = dialog!!.interactionTarget
        //May not work for defender fleets? unsure.
        if (fleet !is CampaignFleetAPI) return true

        //if (!SCSettings.canNPCsSpawnWithSkills && !SCUtils.hasFleetData(fleet)) return true

        var members = fleet.fleetData.membersListCopy
        var flagship = fleet.flagship
        var automated = flagship?.isAutomated() ?: false
        var isBoss = NPCOfficerGenerator.isBossFleet(fleet)



        var data = SCUtils.getFleetData(fleet)

        var officers = data.getActiveOfficers()
        if (officers.isEmpty()) return true

        //add different text here if automated

        if (isBoss) {
            dialog.textPanel.addPara("The opposing anomaly appears to make use of the following skills:", Misc.getTextColor(), Misc.getHighlightColor(), "anomaly", "skills")
        }
        else if (!automated) {
            dialog.textPanel.addPara("The opposing fleet has the following executive officers active within their command: ", Misc.getTextColor(), Misc.getHighlightColor(), "executive officers")
        } else {
            dialog.textPanel.addPara("The opposing fleets commanding core has the following subroutines, which replicate the roles of executive officers: ", Misc.getTextColor(), Misc.getHighlightColor(), "executive officers")
        }


        var tooltip = dialog!!.textPanel.beginTooltip()


        for (officer in officers) {

            tooltip.addSpacer(10f)

            var width = 500f
            var height = 96f
            var panel = Global.getSettings().createCustom(width, height, null)
            tooltip.addCustom(panel, 0f)
            var element = panel.createUIElement(width, height, false)
            panel.addUIElement(element)

            var aptitudePlugin = officer.getAptitudePlugin()
            var activeWithoutOrigin = officer.getActiveSkillPlugins().filter { it.getId() != aptitudePlugin.getOriginSkillId() }

            element.addSpacer(10f)

            var officerPickerElement = SCOfficerPickerElement(officer?.person, aptitudePlugin.getColor(), element, 80f, 80f)
            //officerPickerElement.position.inTL(10f, 10f)

            officerPickerElement.onHoverEnter {
                officerPickerElement.playScrollSound()
            }

            officerPickerElement.onClick {
                officerPickerElement.playClickSound()
            }

            var paraElement = element.addLunaElement(100f, 20f).apply {
                renderBorder = false
                renderBackground = false
            }
            paraElement.elementPanel.position.aboveMid(officerPickerElement.elementPanel, 0f)

            paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
            var aptitudePara = paraElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
            aptitudePara.position.inTL(paraElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, paraElement.height  -aptitudePara.computeTextHeight(aptitudePara.text)-5)

           /* var officerUnderline = SkillUnderlineElement(aptitudePlugin.getColor(), 2f, element, 72f)
            officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)*/

            var offset = 6f
            var offsetElement = element.addLunaElement(0f, 0f)
            offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -8f)

            var background = DialogAptitudeBackgroundElement(aptitudePlugin.getColor(), element, 7f)
            background.elementPanel.position.belowLeft(offsetElement.elementPanel, offset)

            var originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())
            var originSkillElement = SkillWidgetElement(originSkill!!.id, aptitudePlugin.id, true, false, true, originSkill!!.iconPath, "leadership1", aptitudePlugin.getColor(), element, 58f, 58f)
            element.addTooltipTo(SCSkillTooltipCreator(data, originSkill.getPlugin(), aptitudePlugin, 0, false), originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
            //originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
            originSkillElement.elementPanel.position.rightOfMid(background.elementPanel, 20f)


            originSkillElement.onClick {
                originSkillElement.playClickSound()
            }

            /*var originGap = SkillGapElement(aptitudePlugin.getColor(), element, 64f)
            originGap.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 0f)
            originGap.renderArrow = true*/





            var previous: CustomPanelAPI = originSkillElement.elementPanel

            if (activeWithoutOrigin.isNotEmpty()) {
                var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), element, 58f)
                seperator.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 3f)
                previous = seperator.elementPanel
            }

            for (skill in activeWithoutOrigin) {
                var isFirst = activeWithoutOrigin.first() == skill
                var isLast = activeWithoutOrigin.last() == skill

                var skillElement = SkillWidgetElement(skill.getId(), aptitudePlugin.id, true, false, true, skill.getIconPath(), "", aptitudePlugin.getColor(), element, 58f, 58f)

                skillElement.onClick {
                    skillElement.playClickSound()
                }

                var tooltip = SCSkillTooltipCreator(data, skill, aptitudePlugin, 0, false)
                element.addTooltipTo(tooltip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)


                skillElement.elementPanel.position.rightOfTop(previous, 3f)

                if (!isLast) {
                    var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), element, 58f)
                    seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                    previous = seperator.elementPanel
                }
            }
        }


        //tooltip.addPara("Test", 0f)








        dialog.textPanel.addTooltip()

        return true
    }

}