package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.getHeight
import second_in_command.misc.getWidth
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.AptitudeBackgroundElement
import second_in_command.ui.elements.SCOfficerPickerElement
import second_in_command.ui.elements.SkillWidgetElement
import java.awt.Color

class SCSkillMenuPanel(var parent: UIPanelAPI) {


    lateinit var panel: CustomPanelAPI
    lateinit var element: TooltipMakerAPI
    var width = 0f
    var height = 0f

    fun init() {

        width = parent.getWidth()
        height = parent.getHeight()

        panel = Global.getSettings().createCustom(width, height, null)
        parent.addComponent(panel)

        recreatePanel()

    }

    fun recreatePanel() {

        //Remove Previous
        parent.removeComponent(panel)

        panel = Global.getSettings().createCustom(width, height, null)
        parent.addComponent(panel)
        element = panel.createUIElement(width, height, true)
        panel.addUIElement(element)

        element.addPara("", 0f).position.inTL(20f, 5f)
        element.addPara("Test Paragraph", 0f)
        element.addSpacer(270f)

       /* var previous: CustomPanelAPI? = null
        for (skill in SCSpecStore.getSkillSpecs()) {
            element.addSpacer(5f)
            var next = SkillWidgetElement(false, true, skill.iconPath, Color(107,175,0,255), element, 72f, 72f)
            if (previous != null) {
                next.elementPanel.position.rightOfTop(previous, 6f)
            }
            previous = next.elementPanel
        }*/


        addAptitudePanel()



       // SkillWidgetElement(false, true, "", Color(255, 100, 0), element, 64f, 64f)

    }

    fun addAptitudePanel() {

        var subpanel = Global.getSettings().createCustom(width, height, null)
        element.addCustom(subpanel, 0f)
        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)

        subelement.addSectionHeading("Executive Officers", Alignment.MID, 0f).apply {
            position.inTL(-10f, 0f)
            position.setSize(width-20, 20f)
        }


        subelement.addSpacer(30f)

        addAptitudeRow(subelement, true)

        subelement.addSpacer(30f)

        addAptitudeRow(subelement, false)

        subelement.addSpacer(30f)

        addAptitudeRow(subelement, false)
    }

    fun addAptitudeRow(targetedElelement: TooltipMakerAPI, addTest: Boolean) {
        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        targetedElelement.addCustom(subpanel, 0f)
        var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)

        var officerPickerElement = SCOfficerPickerElement(subelement, 96f, 96f)

        var background = AptitudeBackgroundElement(Misc.getDarkPlayerColor(), subelement)
        background.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)

        if (addTest) {
            var previous: CustomPanelAPI? = null
            for (skill in SCSpecStore.getSkillSpecs()) {
                element.addSpacer(5f)
                var next = SkillWidgetElement(false, true, skill.iconPath, Misc.getDarkPlayerColor(), subelement, 72f, 72f)
                if (previous != null) {
                    next.elementPanel.position.rightOfTop(previous, 16f)
                }
                if (previous == null) {
                    next.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
                }
                previous = next.elementPanel
        }


       }
    }

}