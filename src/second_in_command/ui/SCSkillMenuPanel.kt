package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.misc.getHeight
import second_in_command.misc.getWidth
import second_in_command.specs.*
import second_in_command.ui.elements.*

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

        var officer = SCUtils.createRandomSCOfficer("sc_test_aptitude1")

        addAptitudeRow(subelement, officer)

        subelement.addSpacer(30f)

        addAptitudeRow(subelement, null)

        subelement.addSpacer(30f)

        addAptitudeRow(subelement, null)
    }

    fun addAptitudeRow(targetedElelement: TooltipMakerAPI, officer: SCOfficer?) {
        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        targetedElelement.addCustom(subpanel, 0f)
        var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)

        var color = Misc.getDarkPlayerColor()

        if (officer != null) {
            color = officer.getAptitudePlugin().getColor()
        }

        var officerPickerElement = SCOfficerPickerElement(officer, color, subelement, 96f, 96f)
        var background = AptitudeBackgroundElement(color, subelement)
        background.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)

        if (officer == null) {
            return
        }

        var aptitudePlugin = officer.getAptitudePlugin()
        aptitudePlugin.clearSections()
        aptitudePlugin.createSections()

        var sections = aptitudePlugin.getSections()

        var originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())
        var originSkillElement = SkillWidgetElement(originSkill!!.id, true, false, originSkill!!.iconPath, "leadership1", aptitudePlugin.getColor(), subelement, 72f, 72f)
        originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)

        var originGap = SkillGapElement(aptitudePlugin.getColor(), subelement)
        originGap.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 0f)
        originGap.renderArrow = true

        var previousSections = ArrayList<SCAptitudeSection>()
        var skillElements = ArrayList<SkillWidgetElement>()
        var previous: CustomPanelAPI = originGap.elementPanel
        for (section in sections) {

            var isLastSection = sections.last() == section

            section.previousUISections.addAll(previousSections)
            previousSections.add(section)

            var skills = section.getSkills()
            for (skill in skills) {
                var skillSpec = SCSpecStore.getSkillSpec(skill)
                var skillPlugin = skillSpec!!.getPlugin()

                var isFirst = skills.first() == skill
                var isLast = skills.last() == skill

                var canChangeState = true
                if (officer.activeSkillIDs.contains(skill)) {
                    canChangeState = false
                }

                var skillElement = SkillWidgetElement(skill, false, canChangeState, skillPlugin!!.getIconPath(), section.soundId, aptitudePlugin.getColor(), subelement, 72f, 72f)
                skillElements.add(skillElement)
                section.activeSkillsInUI.add(skillElement)




                if (isFirst) {
                    skillElement.elementPanel.position.rightOfTop(previous, 0f)
                } else {
                    skillElement.elementPanel.position.rightOfTop(previous, 3f)
                }



                if (!isLast) {
                    var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), subelement)
                    seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                    previous = seperator.elementPanel
                }
                else if (!isLastSection) {
                    var gap = SkillGapElement(aptitudePlugin.getColor(), subelement)
                    gap.elementPanel.position.rightOfTop(skillElement.elementPanel, 0f)
                    previous = gap.elementPanel

                    var nextIndex = sections.indexOf(section) + 1
                    var nextSection = sections.getOrNull(nextIndex)
                    if (nextSection != null) {
                        nextSection.uiGap = gap
                    }

                }


            }
        }


        for (section in sections) {
            recalculateSectionRequirements(sections)
        }

        /*var count = getActiveSkillCount(skillElements)
        recalculateSectionRequirements(count, sections)*/

        for (skillElement in skillElements) {
            skillElement.onClick {

                var section = getSkillsSection(skillElement.id, sections)
                recalculateSectionRequirements(sections)

                if (skillElement.canChangeState) {

                    if (!skillElement.activated) {
                        skillElement.playSound(skillElement.soundId)
                    }
                    else {
                        skillElement.playSound("ui_char_decrease_skill")
                    }

                    skillElement.activated = !skillElement.activated
                } else {
                    skillElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                }

                recalculateSectionRequirements(sections)
            }
        }



        var levelPara = subelement.addPara("Skill Points: 1", 0f)
        levelPara.position.aboveLeft(officerPickerElement.elementPanel, 4f)



        /*if (addTest) {
            var previous: CustomPanelAPI? = null
            for (skill in SCSpecStore.getSkillSpecs()) {
                element.addSpacer(5f)
                var next = SkillWidgetElement(false, true, skill.iconPath, Color(107,175,0,255), subelement, 72f, 72f)

                if (previous != null) {
                    next.elementPanel.position.rightOfTop(previous, 3f)
                }
                if (previous == null) {
                    next.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
                }

                var seperator = SkillSeperatorElement(Color(107,175,0,255), subelement)
                seperator.elementPanel.position.rightOfTop(next.elementPanel, 3f)

                previous = seperator.elementPanel
            }
        }*/
    }

    fun handleSkillAssignment(skillSpec: SCSkillSpec, skillPlugin: SCBaseSkillPlugin, element: SkillWidgetElement) {

    }

    fun getActiveSkillCount(sections: ArrayList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun recalculateSectionRequirements(sections: ArrayList<SCAptitudeSection>) {
        for (section in sections) {

            var count = getActiveSkillCount(section.previousUISections)

            if (section.requiredPreviousSkills <= count) {
                section.uiGap?.renderArrow = true

                for (skillElement in section.activeSkillsInUI) {
                    skillElement.canChangeState = true
                }
            }
            else {
                section.uiGap?.renderArrow = false

                for (skillElement in section.activeSkillsInUI) {
                    skillElement.activated = false
                    skillElement.canChangeState = false
                }
            }

        }
    }

    fun getSkillsSection(skillId: String, sections: ArrayList<SCAptitudeSection>) : SCAptitudeSection? {
        return sections.find { it.getSkills().contains(skillId) }
    }

}