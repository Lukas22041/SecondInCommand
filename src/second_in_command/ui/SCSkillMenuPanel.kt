package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.clearChildren
import second_in_command.misc.getHeight
import second_in_command.misc.getWidth
import second_in_command.specs.*
import second_in_command.ui.elements.*
import second_in_command.ui.tooltips.OfficerTooltipCreator
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCSkillMenuPanel(var parent: UIPanelAPI, var data: SCData) {


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

        var descriptionPanel = panel.createCustomPanel(width, height, null)
        panel.addComponent(descriptionPanel)
        var descriptionElement = descriptionPanel.createUIElement(width, height, false)
        descriptionPanel.addUIElement(descriptionElement)
        descriptionElement.position.inTL(20f, 40f)

        var para = descriptionElement.addPara("The character menu allows investing skill points in to different skills of your choice.  \n" +
                "It is separated in to two sections, one for your personal skills, and one for the skills of your executive officers. \n" +
                "\n" +
                "Executive officers can be found throughout the world, and all of them have an aptitude they excel at.\n" +
                "You can assign up to three executive officers at a time, each one provides a whole row of skills to choose from. \n" +
                "Some officers may only be re-assigned when docked at a colony. \n" +
                "", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "skill points", "executive officers", "all of them have an aptitude they excel at", "up to three")


        //element.addPara("Test Paragraph", 0f)

       /* var previous: CustomPanelAPI? = null
        for (skill in SCSpecStore.getSkillSpecs()) {
            element.addSpacer(5f)
            var next = SkillWidgetElement(false, true, skill.iconPath, Color(107,175,0,255), element, 72f, 72f)
            if (previous != null) {
                next.elementPanel.position.rightOfTop(previous, 6f)
            }
            previous = next.elementPanel
        }*/

        addPlayerAptitudePanel()

        addAptitudePanel()



       // SkillWidgetElement(false, true, "", Color(255, 100, 0), element, 64f, 64f)

    }

    fun addPlayerAptitudePanel() {
        var playerPanel = SCPlayerAptitudePanel(this, data)
        playerPanel.init()
    }

    fun addAptitudePanel() {

        var subpanel = Global.getSettings().createCustom(width, height, null)
        element.addCustom(subpanel, 0f)
        subpanel.position.inTL(20f, 285+5f+15)
        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)

        subelement.addSectionHeading("Executive Officers", Alignment.MID, 0f).apply {
            position.inTL(-10f, 0f)
            position.setSize(width-20, 20f)
        }


        subelement.addSpacer(30f)

        addAptitudeRowParent(subelement, data.getOfficerInSlot(0), 0)

        subelement.addSpacer(30f)

        addAptitudeRowParent(subelement, data.getOfficerInSlot(1), 1)

        subelement.addSpacer(30f)

        addAptitudeRowParent(subelement, data.getOfficerInSlot(2), 2)
    }

    fun addAptitudeRowParent(targetedElelement: TooltipMakerAPI, officer: SCOfficer?, slotId: Int) {
        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        targetedElelement.addCustom(subpanel, 0f)
        /*var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)*/

        recreateAptitudeRow(subpanel, officer, slotId)
    }

    fun recreateAptitudeRow(subpanelParent: CustomPanelAPI, officer: SCOfficer?, slotId: Int) {
        subpanelParent.clearChildren()

        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        subpanel.position.inTL(0f, 0f)
        subpanelParent.addComponent(subpanel)
        var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)

        var color = Misc.getDarkPlayerColor()

        if (officer != null) {
            color = officer.getAptitudePlugin().getColor()
        }

        var officerPickerElement = SCOfficerPickerElement(officer?.person, color, subelement, 96f, 96f)



        var menu = this
        officerPickerElement.onClick {
            if (officerPickerElement.isInEditMode) {
                //officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }

            if (it.isRMBEvent) {
                officerPickerElement.playSound("ui_char_decrease_skill", 1f, 1f)
                data.setOfficerInSlot(slotId, null)

                recreateAptitudeRow(subpanelParent, null, slotId)
                return@onClick
            }

            var pickerMenu = SCOfficerPickerMenuPanel(menu, officerPickerElement, subpanelParent, slotId, data)
            pickerMenu.init()
            officerPickerElement.playClickSound()
        }

        officerPickerElement.onHoverEnter {
            if (!officerPickerElement.isInEditMode) {
                officerPickerElement.playScrollSound()
            }
        }


        subelement.addTooltipTo(OfficerTooltipCreator(officer), officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)

        var offset = 10f
        var offsetElement = subelement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)


        var background = AptitudeBackgroundElement(color, subelement)
        background.elementPanel.position.belowLeft(offsetElement.elementPanel, offset)

        var officerUnderline = SkillUnderlineElement(color, subelement, 96f)
        officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)

        if (officer == null) {
            return
        }


        var aptitudePlugin = officer.getAptitudePlugin()
        aptitudePlugin.clearSections()
        aptitudePlugin.createSections()

        officerPickerElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = officerPickerElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
        aptitudePara.position.inTL(officerPickerElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, -aptitudePara.computeTextHeight(aptitudePara.text)-5)

        var sections = aptitudePlugin.getSections()

        var originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())
        var originSkillElement = SkillWidgetElement(originSkill!!.id, true, false, true, originSkill!!.iconPath, "leadership1", aptitudePlugin.getColor(), subelement, 72f, 72f)
        subelement.addTooltipTo(SCSkillTooltipCreator(originSkill.getPlugin(), aptitudePlugin, 0), originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
        //originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
        originSkillElement.elementPanel.position.rightOfMid(background.elementPanel, 20f)


        var originGap = SkillGapElement(aptitudePlugin.getColor(), subelement)
        originGap.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 0f)
        originGap.renderArrow = true

        var previousSections = ArrayList<SCAptitudeSection>()
        var skillElements = ArrayList<SkillWidgetElement>()
        var previous: CustomPanelAPI = originGap.elementPanel
        for (section in sections) {

            var isLastSection = sections.last() == section
            var canOnlyChooseOne = !section.canChooseMultiple

            var firstSkillThisSection: SkillWidgetElement? = null
            var usedWidth = 0f

            section.previousUISections.addAll(previousSections)
            previousSections.add(section)

            var skills = section.getSkills()
            for (skill in skills) {
                var skillSpec = SCSpecStore.getSkillSpec(skill)
                var skillPlugin = skillSpec!!.getPlugin()

                var isFirst = skills.first() == skill
                var isLast = skills.last() == skill

                var preacquired = false
                var activated = false
                if (officer.activeSkillIDs.contains(skill)) {
                    preacquired = true
                    activated = true
                }

                var skillElement = SkillWidgetElement(skill, activated, !preacquired, preacquired, skillPlugin!!.getIconPath(), section.soundId, aptitudePlugin.getColor(), subelement, 72f, 72f)
                skillElements.add(skillElement)
                section.activeSkillsInUI.add(skillElement)
                usedWidth += 72f

                var tooltip = SCSkillTooltipCreator(skillPlugin, aptitudePlugin, section.requiredPreviousSkills)
                subelement.addTooltipTo(tooltip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
                section.tooltips.add(tooltip)

                if (firstSkillThisSection == null) {
                    firstSkillThisSection = skillElement
                }

                if (isFirst) {
                    skillElement.elementPanel.position.rightOfTop(previous, 0f)
                } else {
                    skillElement.elementPanel.position.rightOfTop(previous, 3f)
                    usedWidth += 3f
                }



                if (!isLast) {
                    var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), subelement)
                    seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                    previous = seperator.elementPanel
                    usedWidth += 3f
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

                if (canOnlyChooseOne) {
                    var underline = SkillUnderlineElement(color, subelement, usedWidth)
                    underline.position.belowLeft(firstSkillThisSection.elementPanel, 2f)
                }


            }
        }


        for (section in sections) {
            recalculateSectionRequirements(officer, sections, skillElements)
        }

        /*var count = getActiveSkillCount(skillElements)
        recalculateSectionRequirements(count, sections)*/

        for (skillElement in skillElements) {
            skillElement.onClick {

                var section = getSkillsSection(skillElement.id, sections)
                recalculateSectionRequirements(officer, sections, skillElements)

                if (skillElement.canChangeState && !skillElement.preAcquired) {

                    enterEditMode(subpanelParent, officer, officerPickerElement, skillElements, slotId)

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

                recalculateSectionRequirements(officer, sections, skillElements)

                if (officer.activeSkillIDs.count() == sections.sumOf { it.activeSkillsInUI.count { it.activated } }) {
                   /* officerPickerElement.isInEditMode = false
                    officerPickerElement.innerElement.clearChildren()*/
                    exitEditMode(subpanelParent, officer, officerPickerElement, slotId)
                }
            }
        }

        var paraAnchorElement = subelement.addLunaElement(0f, 0f)
        paraAnchorElement.position.aboveLeft(originSkillElement.elementPanel, 6f)

        var spRemaining = calculateRemainingSP(officer, skillElements)


        var officerPara = subelement.addPara("${officer.person.nameString} - $spRemaining SP", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining")
        officerPara.position.rightOfBottom(paraAnchorElement.elementPanel, 0f)

        paraAnchorElement.advance {
            spRemaining = calculateRemainingSP(officer, skillElements)

            var hlColor = Misc.getHighlightColor()
            if (spRemaining == 0) hlColor = Misc.getGrayColor()

            officerPara.text = "${officer.person.nameString} - $spRemaining SP"
            officerPara.setHighlight("$spRemaining")
            officerPara.setHighlightColor(hlColor)
        }


      /*  var spPara = subelement.addPara("- $spRemaining", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining")
        spPara.position.rightOfBottom(paraAnchorElement.elementPanel, 120f)*/

    }

    fun calculateRemainingSP(offficer: SCOfficer, skills: ArrayList<SkillWidgetElement>) : Int {
        var newSkills = skills.filter { !offficer.activeSkillIDs.contains(it.id) && it.activated }
        return offficer.skillPoints - newSkills.count()
    }

    fun saveSkillDataToCharacter(officer: SCOfficer, skillElements: ArrayList<SkillWidgetElement>) {
        var activeSkills = skillElements.filter { it.activated }.map { it.id }

        var spRemaining = calculateRemainingSP(officer, skillElements)

        officer.activeSkillIDs.addAll(activeSkills)
        officer.skillPoints = spRemaining
    }

    fun enterEditMode(subpanelParent: CustomPanelAPI, offficer: SCOfficer, picker: SCOfficerPickerElement, skillElements: ArrayList<SkillWidgetElement>, slotId: Int) {
        if (picker.isInEditMode) return
        picker.isInEditMode = true

        picker.innerElement.addSpacer(12f)

        var confirmButton = ConfirmCancelButton(picker.color, picker.innerElement, 86f, 30f).apply {
            addText("Confirm")
            centerText()

            onClick {
                playSound(Sounds.STORY_POINT_SPEND)
                saveSkillDataToCharacter(offficer, skillElements)
                exitEditMode(subpanelParent, offficer, picker, slotId)
            }
        }
        confirmButton.elementPanel.position.inTL(5f, 12f)


        var cancelButton = ConfirmCancelButton(picker.color, picker.innerElement, 86f, 30f).apply {
            addText("Cancel")
            centerText()

            onClick {
                playSound("ui_char_decrease_skill", 1f, 1f)
                exitEditMode(subpanelParent, offficer, picker, slotId)
            }
        }

        cancelButton.elementPanel.position.belowLeft(confirmButton.elementPanel, 12f)

    }

    fun exitEditMode(subpanelParent: CustomPanelAPI, offficer: SCOfficer, picker: SCOfficerPickerElement, slotId: Int) {
        recreateAptitudeRow(subpanelParent, offficer, slotId)
    }

    fun getActiveSkillCount(sections: ArrayList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun recalculateSectionRequirements(officer: SCOfficer, sections: ArrayList<SCAptitudeSection>, skillElements: ArrayList<SkillWidgetElement>) {
        for (section in sections) {

            var count = getActiveSkillCount(section.previousUISections)

            if (section.requiredPreviousSkills <= count) {
                section.uiGap?.renderArrow = true
                section.tooltips.forEach { it.sectionMeetsRequirements = true }

                for (skillElement in section.activeSkillsInUI) {
                    if (skillElement.preAcquired) continue
                    skillElement.canChangeState = true
                }
            }
            else {
                section.uiGap?.renderArrow = false
                section.tooltips.forEach { it.sectionMeetsRequirements = false }

                for (skillElement in section.activeSkillsInUI) {
                    if (skillElement.preAcquired) continue
                    skillElement.activated = false
                    skillElement.canChangeState = false
                }
            }

            if (!section.canChooseMultiple) {
                if (section.activeSkillsInUI.any { it.activated }) {
                    for (skillElement in section.activeSkillsInUI) {
                        if (skillElement.preAcquired) continue
                        if (!skillElement.activated) {
                            skillElement.canChangeState = false
                        }
                    }
                }
            }


        }

        if (calculateRemainingSP(officer, skillElements) <= 0) {
            for (skillElement in skillElements) {
                if (skillElement.activated) continue
                skillElement.canChangeState = false
            }
        }
    }

    fun getSkillsSection(skillId: String, sections: ArrayList<SCAptitudeSection>) : SCAptitudeSection? {
        return sections.find { it.getSkills().contains(skillId) }
    }


}