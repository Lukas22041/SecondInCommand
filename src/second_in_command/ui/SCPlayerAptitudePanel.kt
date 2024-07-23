package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import second_in_command.SCData
import second_in_command.misc.VanillaSkillTooltip
import second_in_command.misc.clearChildren
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.tooltips.OfficerTooltipCreator
import second_in_command.ui.tooltips.PlayerTooltipCreator
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCPlayerAptitudePanel(var menu: SCSkillMenuPanel, var data: SCData)  {

    fun init() {

        var width = menu.width
        var height = menu.height

        var subpanel = Global.getSettings().createCustom(width, height, null)
        menu.element.addCustom(subpanel, 0f)
        subpanel.position.inTL(25f, 180f)

        recreateAptitudePanel(subpanel)
    }

    fun recreateAptitudePanel(subpanel: CustomPanelAPI) {
        subpanel.clearChildren()

        var width = menu.width
        var height = menu.height

        var aptitudePlugin = SCSpecStore.getAptitudeSpec("sc_fake_combat_aptitude")!!.getPlugin()

        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)

        var acquiredSkillsIds = data.player.stats.skillsCopy.filter { it.level >= 2 }.map { it.skill.id }

        var player = Global.getSector().playerPerson
        var color = Global.getSettings().getSkillSpec("aptitude_combat").governingAptitudeColor
        var sections = ArrayList<SCAptitudeSection>()

        var section1 = SCAptitudeSection(true, 0, "combat2")
        section1.addSkill("helmsmanship")
        section1.addSkill("combat_endurance")
        section1.addSkill("impact_mitigation")
        section1.addSkill("damage_control")
        section1.addSkill("field_modulation")
        section1.addSkill("point_defense")
        section1.addSkill("target_analysis")
        section1.addSkill("ballistic_mastery")
        sections.add(section1)

        var section2 = SCAptitudeSection(false, 4, "combat4")
        section2.addSkill("systems_expertise")
        section2.addSkill("missile_specialization")
        sections.add(section2)

        var officerPickerElement = SCOfficerPickerElement(data!!.player, color, subelement, 96f, 96f)
        officerPickerElement.onHoverEnter {
            if (!officerPickerElement.isInEditMode) {
                officerPickerElement.playScrollSound()
            }
        }

        officerPickerElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = officerPickerElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
        aptitudePara.position.inTL(officerPickerElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, -aptitudePara.computeTextHeight(aptitudePara.text)-5)

        subelement.addTooltipTo(PlayerTooltipCreator(player), officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)


        var offset = 10f
        var offsetElement = subelement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)

        var background = AptitudeBackgroundElement(color, subelement)
        background.elementPanel.position.belowLeft(offsetElement.elementPanel, offset)

        var officerUnderline = SkillUnderlineElement(color, subelement, 96f)
        officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)

        var originSkill = SCSpecStore.getSkillSpec("sc_combat_aptitude_skill")
        var originSkillElement = SkillWidgetElement(originSkill!!.id, true, false, true, originSkill!!.iconPath, "leadership1", color, subelement, 72f, 72f)
        //originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
        originSkillElement.elementPanel.position.rightOfMid(background.elementPanel, 20f)
        subelement.addTooltipTo(SCSkillTooltipCreator(originSkill.getPlugin(), SCSpecStore.getAptitudeSpec("sc_fake_combat_aptitude")!!.getPlugin(), 0, false), originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)

        var originGap = SkillGapElement(color, subelement)
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
                var skillSpec = Global.getSettings().getSkillSpec(skill)

                var isFirst = skills.first() == skill
                var isLast = skills.last() == skill

                var preacquired = false
                var activated = false
                if (acquiredSkillsIds.contains(skill)) {
                    preacquired = true
                    activated = true
                }

                var skillElement = SkillWidgetElement(skill, activated, !preacquired, preacquired, skillSpec.spriteName, section.soundId, color, subelement, 72f, 72f)
                skillElements.add(skillElement)
                section.activeSkillsInUI.add(skillElement)
                usedWidth += 72f

                var level = 0
                if (activated) {

                }

                var skillTooltip = VanillaSkillTooltip.addToTooltip(subelement, player, skillSpec, section.requiredPreviousSkills)
                section.vanillaTooltips.add(skillTooltip)

                skillElement.advance {
                    if (skillElement.activated) {
                        skillTooltip.level = 2f
                    } else {
                        skillTooltip.level = 0f
                    }
                }

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
                    var seperator = SkillSeperatorElement(color, subelement)
                    seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                    previous = seperator.elementPanel
                    usedWidth += 3f
                }
                else if (!isLastSection) {
                    var gap = SkillGapElement(color, subelement)
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
            recalculateSectionRequirements(sections, skillElements)
        }

        for (skillElement in skillElements) {
            skillElement.onClick {

                var section = getSkillsSection(skillElement.id, sections)
                recalculateSectionRequirements(sections, skillElements)

                if (skillElement.canChangeState && !skillElement.preAcquired) {

                    enterEditMode(subpanel, officerPickerElement, skillElements)

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

                recalculateSectionRequirements(sections, skillElements)

                if (data.player.stats.skillsCopy.filter { it.level >= 2 }.map { it.skill.id }.count() == sections.sumOf { it.activeSkillsInUI.count { it.activated } }) {
                    /* officerPickerElement.isInEditMode = false
                     officerPickerElement.innerElement.clearChildren()*/
                    exitEditMode(subpanel)
                }
            }
        }

        var paraAnchorElement = subelement.addLunaElement(0f, 0f)
        paraAnchorElement.position.aboveLeft(originSkillElement.elementPanel, 6f)

        var spRemaining = calculateRemainingSP(skillElements)

        var officerPara = subelement.addPara("${data.player.nameString} - $spRemaining SP", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining")
        officerPara.position.rightOfBottom(paraAnchorElement.elementPanel, 0f)

        paraAnchorElement.advance {
            spRemaining = calculateRemainingSP(skillElements)

            var hlColor = Misc.getHighlightColor()
            if (spRemaining == 0) hlColor = Misc.getGrayColor()

            officerPara.text = "${data.player.nameString} - $spRemaining SP"
            officerPara.setHighlight("$spRemaining")
            officerPara.setHighlightColor(hlColor)
        }

    }

    fun calculateRemainingSP(skills: ArrayList<SkillWidgetElement>) : Int {
        var acquiredSkillsIds = data.player.stats.skillsCopy.filter { it.level >= 2 }.map { it.skill.id }
        var newSkills = skills.filter { !acquiredSkillsIds.contains(it.id) && it.activated }
        return data.player.stats.points - newSkills.count()
    }

    fun saveSkillDataToCharacter(skillElements: ArrayList<SkillWidgetElement>) {
        var activeSkills = skillElements.filter { it.activated }.map { it.id }

        var spRemaining = calculateRemainingSP(skillElements)
        var stats = data.player.stats

        for (active in activeSkills) {
            stats.setSkillLevel(active, 2f)
        }

        stats.points = spRemaining
    }

    fun enterEditMode(subpanelParent: CustomPanelAPI, picker: SCOfficerPickerElement, skillElements: ArrayList<SkillWidgetElement>) {
        if (picker.isInEditMode) return
        picker.isInEditMode = true

        picker.innerElement.addSpacer(12f)

        var confirmButton = ConfirmCancelButton(picker.color, picker.innerElement, 86f, 30f).apply {
            addText("Confirm")
            centerText()

            onClick {
                playSound(Sounds.STORY_POINT_SPEND)
                saveSkillDataToCharacter(skillElements)
                exitEditMode(subpanelParent)

                if (Global.getSector().playerFleet?.fleetData != null) {
                    Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
                }
            }
        }
        confirmButton.elementPanel.position.inTL(5f, 12f)

        var cancelButton = ConfirmCancelButton(picker.color, picker.innerElement, 86f, 30f).apply {
            addText("Cancel")
            centerText()

            onClick {
                playSound("ui_char_decrease_skill", 1f, 1f)
                exitEditMode(subpanelParent)
            }
        }

        cancelButton.elementPanel.position.belowLeft(confirmButton.elementPanel, 12f)

    }

    fun exitEditMode(subpanelParent: CustomPanelAPI) {
        recreateAptitudePanel(subpanelParent)
    }

    fun getActiveSkillCount(sections: ArrayList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun recalculateSectionRequirements(sections: ArrayList<SCAptitudeSection>, skillElements: ArrayList<SkillWidgetElement>) {
        for (section in sections) {

            var count = getActiveSkillCount(section.previousUISections)

            if (section.requiredPreviousSkills <= count) {
                section.uiGap?.renderArrow = true
                section.vanillaTooltips.forEach { it.sectionMeetsRequirements = true }

                for (skillElement in section.activeSkillsInUI) {
                    if (skillElement.preAcquired) continue
                    skillElement.canChangeState = true
                }
            }
            else {
                section.uiGap?.renderArrow = false
                section.vanillaTooltips.forEach { it.sectionMeetsRequirements = false }

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

        if (calculateRemainingSP(skillElements) <= 0) {
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