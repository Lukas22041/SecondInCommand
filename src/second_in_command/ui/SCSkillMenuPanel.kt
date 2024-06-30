package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import second_in_command.misc.getHeight
import second_in_command.misc.getWidth
import second_in_command.specs.SCSpecStore
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
        element = panel.createUIElement(width, height, false)
        panel.addUIElement(element)

        element.addPara("", 0f).position.inTL(20f, 5f)
        element.addPara("Test Paragraph", 0f)

        var previous: CustomPanelAPI? = null
        for (skill in SCSpecStore.getSkillSpecs()) {
            element.addSpacer(5f)
            var next = SkillWidgetElement(false, true, skill.iconPath, Color(107,175,0,255), element, 72f, 72f)
            if (previous != null) {
                next.elementPanel.position.rightOfTop(previous, 6f)
            }
            previous = next.elementPanel
        }

       // SkillWidgetElement(false, true, "", Color(255, 100, 0), element, 64f, 64f)

    }

}