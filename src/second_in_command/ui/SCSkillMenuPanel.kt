package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.RepLevel
import com.fs.starfarer.api.characters.SkillSpecAPI
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaExtensions.addLunaTextfield
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.*
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.panels.AssosciatesManagePanelPlugin
import second_in_command.ui.tooltips.OfficerTooltipCreator
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCSkillMenuPanel(var parent: UIPanelAPI,
                       var data: SCData,
                       var title: Boolean/* var seedTextElement: LabelAPI, var seedElement: UIComponentAPI, var copyButton: UIComponentAPI*/) {


    lateinit var panel: CustomPanelAPI
    lateinit var element: TooltipMakerAPI
    var width = 0f
    var height = 0f
    var isAtColony = false

    var rowParents = HashMap<Int, CustomPanelAPI>()
    var subpanel: CustomPanelAPI? = null

    companion object {
        var crCost = 0.20f
        var lastAptitudeScrollerY = 0f
        var lastVanillaAptitudeScrollerY = 0f
        var isAptitudeTabSelected = true
    }

    fun init() {

        var interaction = Global.getSector().campaignUI.currentInteractionDialog
        if (interaction != null && interaction.interactionTarget != null) {
            var interactionMarket = interaction.interactionTarget.market
            if (interactionMarket != null && !interactionMarket.isPlanetConditionMarketOnly) {
                var faction = interactionMarket.faction
                if (faction.relToPlayer.isAtWorst(RepLevel.INHOSPITABLE)) {
                    isAtColony = true
                }
            }
        }

        if (!isAtColony) {
            var fleet = Global.getSector().playerFleet
            var system = fleet?.containingLocation

            if (system != null) {
                var allowedDistance = 1000f

                var markets = system.customEntities.map { it.market } + system.planets.map { it.market }
                markets = markets.filter { it != null && !it.isPlanetConditionMarketOnly && it.faction != null && it.primaryEntity != null }
                markets = markets.filter {  it.faction.relToPlayer.isAtWorst(RepLevel.INHOSPITABLE) &&
                        MathUtils.getDistance(it.primaryEntity.location, fleet.location) <= allowedDistance }

                if (markets.isNotEmpty()) {
                    isAtColony = true
                }
            }

        }



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

        //addPlayerAptitudePanel()

        var playerPanel = SCPlayerPanel(this, data)
        playerPanel.init()


        addHeaderPanel()
        //addAptitudePanel()

      /*  element.addCustom(seedTextElement as UIComponentAPI, 0f)

        element.addCustom(seedElement as UIComponentAPI, 0f)

        element.addCustom(copyButton as UIComponentAPI, 0f)

        copyButton.position.inTL(width-copyButton.getWidth()-26, height-copyButton.getHeight()-7)
        seedElement.position.leftOfTop(copyButton, 3f)
        seedTextElement.position.leftOfTop(seedElement, 3f)*/



       // SkillWidgetElement(false, true, "", Color(255, 100, 0), element, 64f, 64f)

    }

    fun addHeaderPanel() {

        var headerSubpanel = Global.getSettings().createCustom(width, height, null)
        element.addCustom(headerSubpanel, 0f)
        headerSubpanel!!.position.inTL(20f, 285+5f+15)


        var headerElement = headerSubpanel!!.createUIElement(width, 20f, false)
        headerSubpanel!!.addUIElement(headerElement)
        headerElement.position.inTL(0f, 0f)

        var isVanillaSectionEnabled = getVanillaSystemAptitudes().isNotEmpty()

        headerElement.addSectionHeading(if (isVanillaSectionEnabled) "" else "Executive Officers", Alignment.MID, 0f).apply {
            position.inTL(-10f, 0f)
            position.setSize(width-20, 20f)
        }

        if (isVanillaSectionEnabled) {
            var executiveButton = ClickableTextButton("Executive Officers", Misc.getBasePlayerColor(), headerElement, 200f, 20f)
            executiveButton.position.inTL(headerElement.widthSoFar/2-(executiveButton.width)-15, 1f)

            headerElement.addTooltip(executiveButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 450f) { tooltip ->
                tooltip.addPara("Executive officers provide fleetwide skills. They can be hired at the comm directory of colonies or found in derelict ships. " +
                        "")
            }


            var otherSkillsButton = ClickableTextButton("Non-Executive Skills", Misc.getBasePlayerColor(), headerElement, 200f, 20f)
            otherSkillsButton.position.inTL(headerElement.widthSoFar/2-15, 1f)

            headerElement.addTooltip(otherSkillsButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 450f) { tooltip ->
                tooltip.addPara("Section for modded skills not managed by Second-in-Command which use the vanilla skill system. Skills displayed here can not be acquired through skill points, " +
                        "and can only be viewed, but some mods may provide separate ways to unlock some of those skills. ")
            }

            executiveButton.onClick {
                executiveButton.playClickSound()
                if (!isAptitudeTabSelected) {
                    isAptitudeTabSelected = true
                    executiveButton.active = true
                    otherSkillsButton.active = false
                    addAptitudePanel()
                }
            }

            otherSkillsButton.onClick {
                otherSkillsButton.playClickSound()
                if (isAptitudeTabSelected) {
                    isAptitudeTabSelected = false
                    executiveButton.active = false
                    otherSkillsButton.active = true
                    addVanillaSkillsPanel()
                }
            }

            if (isAptitudeTabSelected) {
                executiveButton.active = true
                addAptitudePanel()
            } else {
                addVanillaSkillsPanel()
                otherSkillsButton.active = true
            }
        } else {
            addAptitudePanel()
        }


    }


    fun addAptitudePanel() {

        if (subpanel != null) {
            element.removeComponent(subpanel)
        }

        subpanel = Global.getSettings().createCustom(width, height, null)
        element.addComponent(subpanel)
        subpanel!!.position.inTL(20f, 285+5f+15)


        var scrollerPanel = Global.getSettings().createCustom(width - 20, 400f, null)
        subpanel!!.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(0f, 25f)
        if (SCSettings.enable4thSlot) scrollerPanel.position.inTL(-10f, 25f)



        var subelement = scrollerPanel.createUIElement(width - 20, 400f, true)

        if (!title) {

            subelement.addSpacer(23f)

            addAptitudeRowParent(subelement, data.getOfficerInSlot(0), 0)

            subelement.addSpacer(30f)

            addAptitudeRowParent(subelement, data.getOfficerInSlot(1), 1)

            subelement.addSpacer(30f)

            addAptitudeRowParent(subelement, data.getOfficerInSlot(2), 2)

            if (SCSettings.enable4thSlot) {
                subelement.addSpacer(30f)

                addAptitudeRowParent(subelement, data.getOfficerInSlot(3), 3)

                subelement.addSpacer(23f)

                subelement.addLunaElement(0f, 0f).advance {
                    lastAptitudeScrollerY = subelement.externalScroller.yOffset
                }
            }

        } else {

            subelement.addSpacer(20f)

            subelement.addPara("Executive Officers can only be selected in the campaign.", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        }

        scrollerPanel.addUIElement(subelement)
        if (SCSettings.enable4thSlot) {
            subelement.externalScroller.yOffset = lastAptitudeScrollerY
        }


    }

    fun addAptitudeRowParent(targetedElelement: TooltipMakerAPI, officer: SCOfficer?, slotId: Int) {
        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        targetedElelement.addCustom(subpanel, 0f)
        /*var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)*/

        rowParents.put(slotId, subpanel)

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

        var isProgressionMode = SCSettings.progressionMode
        var level = Global.getSector().playerPerson.stats.level
        var progressionLevel = when(slotId) {
            0 -> SCSettings.progressionSlot1Level!!
            1 -> SCSettings.progressionSlot2Level!!
            2 -> SCSettings.progressionSlot3Level!!
            3 -> SCSettings.progressionSlot4Level!!
            else -> 0
        }
        var isLocked = officer == null && isProgressionMode && level < progressionLevel
        var officerPickerElement = SCOfficerPickerElement(officer?.person, color, subelement, 96f, 96f)
        officerPickerElement.isProgressionLocked = isLocked


        var menu = this
        officerPickerElement.onClick {
            if (officerPickerElement.isInEditMode) {
                //officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }

            /*if (!docked && officer?.getAptitudePlugin()?.getRequiresDock() == true) {
                officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }*/

            if (SCUtils.isAssociatesBackgroundActive() && officer != null) {
                openResrictedOfficerManagementPanel(panel, subpanelParent, officer!!)
                officerPickerElement.playClickSound()
                return@onClick
            }

            if (it.isRMBEvent) {

                if (data.getOfficerInSlot(slotId) != null) {
                    officerPickerElement.playSound("ui_char_decrease_skill", 1f, 1f)

                    var officerInSlot = data.getOfficerInSlot(slotId)
                    data.setOfficerInSlot(slotId, null)
                    /* if (officerInSlot != null) {
                         var skills = officerInSlot.getActiveSkillPlugins()

                         if (Global.getSector().playerFleet?.fleetData != null) {
                             for (skill in skills) {
                                 skill.onDeactivation(data)
                             }
                             Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
                         }
                     }*/

                    //data.setOfficerInSlot(slotId, null)

                    checkToApplyCRPenalty()
                    recreateAptitudeRow(subpanelParent, null, slotId)
                }

                return@onClick
            }

            //Dont allow editing a locked slot
            if (isLocked) {
                officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }

            var pickerMenu = SCOfficerPickerMenuPanel(menu, officerPickerElement, subpanelParent, slotId, data, isAtColony)
            pickerMenu.init()
            officerPickerElement.playClickSound()
        }

        officerPickerElement.onHoverEnter {
            if (!officerPickerElement.isInEditMode) {
                officerPickerElement.playScrollSound()
            }
        }

        officerPickerElement.onInput { events ->
            if (officerPickerElement.isHovering && officer != null) {
                for (event in events!!) {
                    if (event.isConsumed) continue
                    if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_R) {
                        event.consume()

                        if (!officerPickerElement.isInEditMode) {

                            var active = officer.getActiveSkillPlugins().filter { it != officer.getAptitudePlugin().originSkillPlugin }
                            var count = active.count()

                            var storyPoints = Global.getSector().playerPerson.stats.storyPoints
                            if (count == 0 || storyPoints <= 3) {
                                officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                                return@onInput
                            }

                            for (skill in active) {
                                officer.skillPoints += 1
                                skill.onDeactivation(data)
                            }

                            officer.activeSkillIDs = mutableSetOf()
                            Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }

                            officerPickerElement.playSound(Sounds.STORY_POINT_SPEND)

                            Global.getSector().playerPerson.stats.storyPoints -= 4

                            recreateAptitudeRow(subpanelParent, officer, slotId)
                        }

                        break
                    }
                }
            }
        }

        if (!isLocked) {
            subelement.addTooltipTo(OfficerTooltipCreator(officer, isAtColony, false), officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)
        } else {
            subelement.addTooltip(officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 350f) { tooltip ->
                tooltip.addPara("This slot is locked until the player has reached level $progressionLevel and can not be used until then.", 0f
                ,Misc.getTextColor(), Misc.getHighlightColor(), "$progressionLevel")
            }
        }

        var offset = 10f
        var offsetElement = subelement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)


        var background = AptitudeBackgroundElement(color, subelement)
        background.elementPanel.position.belowLeft(offsetElement.elementPanel, offset)

        var officerUnderline = SkillUnderlineElement(color, 2f, subelement, 96f)
        officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)

        if (officer == null) {
            return
        }


        var aptitudePlugin = officer.getAptitudePlugin()
        /*aptitudePlugin.clearSections()
        aptitudePlugin.createSections()*/

        var paraElement = subelement.addLunaElement(100f, 20f).apply {
            renderBorder = false
            renderBackground = false
        }
        paraElement.position.aboveLeft(officerPickerElement.elementPanel, 0f)

        paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = paraElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
        aptitudePara.position.inTL(paraElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 3, paraElement.height  -aptitudePara.computeTextHeight(aptitudePara.text)-5)

      /*  officerPickerElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = officerPickerElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
        aptitudePara.position.inTL(officerPickerElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 - 1, -aptitudePara.computeTextHeight(aptitudePara.text)-5)
*/
        var sections = aptitudePlugin.getSections()

        var originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())
        var originSkillElement = SkillWidgetElement(originSkill!!.id, aptitudePlugin.id, true, false, true, originSkill!!.iconPath, "leadership1", aptitudePlugin.getColor(), subelement, 72f, 72f)
        subelement.addTooltipTo(SCSkillTooltipCreator(data, originSkill.getPlugin(), aptitudePlugin, 0, false), originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
        //originSkillElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, 20f)
        originSkillElement.elementPanel.position.rightOfMid(background.elementPanel, 20f)


        originSkillElement.onClick {
            originSkillElement.playClickSound()
        }

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

                var skillElement = SkillWidgetElement(skill, aptitudePlugin.id, activated, !preacquired, preacquired, skillPlugin!!.getIconPath(), section.soundId, aptitudePlugin.getColor(), subelement, 72f, 72f)
                skillElements.add(skillElement)
                section.activeSkillsInUI.add(skillElement)
                usedWidth += 72f

                var tooltip = SCSkillTooltipCreator(data, skillPlugin, aptitudePlugin, section.requiredPreviousSkills, !section.canChooseMultiple)
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
                    var underline = SkillUnderlineElement(color, 2f, subelement, usedWidth)
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


    fun getVanillaSystemAptitudes() : List<SkillSpecAPI> {
        var skills = Global.getSettings().skillIds
        var aptitudes = skills.map { Global.getSettings().getSkillSpec(it) }.filter { it.isAptitudeEffect }
        aptitudes = aptitudes.filter { !it.hasTag("npc_only") }
        aptitudes = aptitudes.filter { it.id != "aptitude_combat" && it.id != "aptitude_leadership" && it.id != "aptitude_technology" && it.id != "aptitude_industry"}
        aptitudes = aptitudes.sortedBy { it.governingAptitudeOrder }
        return aptitudes
    }

    fun addVanillaSkillsPanel() {
        if (subpanel != null) {
            element.removeComponent(subpanel)
        }

        subpanel = Global.getSettings().createCustom(width, height, null)
        element.addComponent(subpanel)
        subpanel!!.position.inTL(20f, 285+5f+15)

        var scrollerPanel = Global.getSettings().createCustom(width - 20, 400f, null)
        subpanel!!.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(-10f, 25f)

        var subelement = scrollerPanel.createUIElement(width - 20, 400f, true)

        var aptitudes = getVanillaSystemAptitudes()

        subelement.addSpacer(10f)
        for (aptitude in aptitudes) {
            addVanillaSkillSection(aptitude, subelement)
        }

        subelement.addLunaElement(0f, 0f).advance {
            lastVanillaAptitudeScrollerY = subelement.externalScroller.yOffset
        }

        scrollerPanel.addUIElement(subelement)
        subelement.externalScroller.yOffset = lastVanillaAptitudeScrollerY

    }

    fun addVanillaSkillSection(aptitude: SkillSpecAPI, targetedElelement: TooltipMakerAPI) {
        var subpanel = Global.getSettings().createCustom(width, 96f, null)
        targetedElelement.addCustom(subpanel, 0f)

        var subelement = subpanel.createUIElement(width, 96f, false)
        subpanel.addUIElement(subelement)

        var color = aptitude.governingAptitudeColor

        var aptitudeElement = VanillaAptitudeSkillWidgetElement(aptitude.id, aptitude.spriteName, color, subelement, 110f, 70f)

        var anchor = subelement.addLunaElement(0f, 0f)
        anchor.position.belowLeft(aptitudeElement.elementPanel, 10f)

        subelement.setParaFont("graphics/fonts/victor14.fnt")
        var namePara = subelement.addPara(aptitude.name, 0f, color, color)
        namePara.position.rightOfMid(anchor.elementPanel, aptitudeElement.width-namePara.computeTextWidth(namePara.text))

        var background = VanillaAptitudeBackgroundElement(color, subelement)
        background.position.rightOfMid(aptitudeElement.elementPanel, 1f)

        var gap = SkillGapElement(color, subelement, 2f)
        gap.renderArrow = true
        gap.position.rightOfMid(aptitudeElement.elementPanel, 10f)


        var skillIds = Global.getSettings().skillIds
        var skills = skillIds.map { Global.getSettings().getSkillSpec(it) }.filter { it.governingAptitudeId == aptitude.governingAptitudeId }
        skills = skills.filter { !it.hasTag("npc_only") && !it.hasTag("ai_core_only") && !it.hasTag("deprecated") && !it.isAptitudeEffect }
        skills = skills.sortedBy { it.order }

        var playerStats = Global.getSector().playerPerson.stats
        var previous: UIPanelAPI = background.elementPanel
        for (skill in skills) {
            var skillLevel = playerStats.getSkillLevel(skill.id)
            var activated = skillLevel >= 1f
            var isFirst = skill == skills.first()
            var isLast = skill == skills.last()

            var skillElement = SkillWidgetElement(skill.id, aptitude.id, activated, activated, false, skill.spriteName, "combat1", color, subelement, 72f, 72f)

            //Only works on combat skills
           /* var skillTooltip = ReflectionUtils.invokeStatic(9, "createSkillTooltip", StandardTooltipV2::class.java,
                skill, playerStats,
                800f, 10f, true, false, 1000, null, null)

            ReflectionUtils.invokeStatic(2, "addTooltipBelow", StandardTooltipV2Expandable::class.java, skillElement.elementPanel, skillTooltip)*/


            //Fake person that will always have the selected skill leveled, so that the description doesnt look off
            val fake = Global.getFactory().createPerson()
            fake.stats.setSkillLevel(skill.id, 2f)
            var tip = VanillaSkillTooltipForVanillaSection(subelement, fake, skill)

            subelement.addTooltipTo(tip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, false)

            skillElement.onClick {
                skillElement.playClickSound()
            }

            if (isFirst) {
                skillElement.position.rightOfMid(previous, 55f)
            } else {
                skillElement.position.rightOfMid(previous, 7f)
            }

            if (!isLast) {
                var seperator = SkillSeperatorElement(color, subelement)
                seperator.position.rightOfTop(skillElement.elementPanel, 3f)

            }
            previous = skillElement.elementPanel

        }

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

    fun enterEditMode(subpanelParent: CustomPanelAPI, officer: SCOfficer, picker: SCOfficerPickerElement, skillElements: ArrayList<SkillWidgetElement>, slotId: Int) {
        if (picker.isInEditMode) return
        picker.isInEditMode = true

        picker.innerElement.addSpacer(12f)

        var confirmButton = ConfirmCancelButton(picker.color, picker.innerElement, 86f, 30f).apply {
            addText("Confirm")
            centerText()

            onClick {
                playSound(Sounds.STORY_POINT_SPEND)
                saveSkillDataToCharacter(officer, skillElements)
                exitEditMode(subpanelParent, officer, picker, slotId)

                var skills = officer!!.getActiveSkillPlugins()

                if (Global.getSector().playerFleet?.fleetData != null) {
                    for (skill in skills) {
                        skill.onActivation(data)
                    }
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
                exitEditMode(subpanelParent, officer, picker, slotId)
            }
        }

        cancelButton.elementPanel.position.belowLeft(confirmButton.elementPanel, 12f)

    }

    fun exitEditMode(subpanelParent: CustomPanelAPI, offficer: SCOfficer, picker: SCOfficerPickerElement, slotId: Int) {
        recreateAptitudeRow(subpanelParent, offficer, slotId)
    }

    fun getActiveSkillCount(sections: MutableList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun recalculateSectionRequirements(officer: SCOfficer, sections: MutableList<SCAptitudeSection>, skillElements: ArrayList<SkillWidgetElement>) {
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

    fun getSkillsSection(skillId: String, sections: MutableList<SCAptitudeSection>) : SCAptitudeSection? {
        return sections.find { it.getSkills().contains(skillId) }
    }


    fun checkToApplyCRPenalty() {
        if (!isAtColony) {
            for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
                member.repairTracker.cr -= crCost
                member.repairTracker.cr = MathUtils.clamp(member.repairTracker.cr, 0.1f, 1f)
            }

            var cost = (crCost * 100).toInt()

            Global.getSector().campaignUI.messageDisplay.addMessage(
                "Applied a $cost% penalty to all ships combat-readiness due to changing officers outside of the range of a colony.",
                Misc.getBasePlayerColor(), "$cost%", Misc.getHighlightColor(),
            )
        }
    }

    fun openResrictedOfficerManagementPanel(panel: CustomPanelAPI, subpanelParent: CustomPanelAPI, officer: SCOfficer) {
        var plugin = AssosciatesManagePanelPlugin(panel)

        var width = 316f
        var height = 170f

        var managementPanel = this.panel.createCustomPanel(width, height, plugin)
        plugin.panel = managementPanel
        panel.addComponent(managementPanel)
        managementPanel.position.inMid()

        var element = managementPanel.createUIElement(width, height, false)
        managementPanel.addUIElement(element)

        var portraitElement = element.addLunaElement(128f, 128f)
        portraitElement.position.inTL(20f, 20f)

        portraitElement.render {
            var path = officer.person.portraitSprite
            var sprite = Global.getSettings().getAndLoadSprite(path)

            sprite.setSize(128f, 128f)
            sprite.alphaMult = 1f
            sprite.setNormalBlend()
            sprite.render(portraitElement.elementPanel.position.x, portraitElement.elementPanel.position.y)

            if (portraitElement.isHovering) {
                sprite.setAdditiveBlend()
                sprite.alphaMult = 0.3f
                sprite.render(portraitElement.elementPanel.position.x, portraitElement.elementPanel.position.y)
            }
        }

        portraitElement.onHoverEnter {
            portraitElement.playScrollSound()
        }

        portraitElement.onClick {
            portraitElement.playClickSound()
            SCOfficerPickerMenuPanel.openPortraitPicker(officer.person, this)
        }

        element.addTooltip(portraitElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 350f ) {
            it.addPara("Click to change the officers portrait. You can pick from any portrait that is available for the player at the start of the game.", 0f)
        }

        var nameElement = element.addLunaTextfield(officer.person.nameString, false, 128f, 30f).apply {
            enableTransparency = true
        }
        //nameElement.position.inTMid(20f)
        nameElement.position.rightOfTop(portraitElement.elementPanel, 20f)

        nameElement.advance {
            var officerName = officer.person.nameString
            if (officerName != nameElement.getText()) {
                var space = nameElement.getText().indexOf(" ")

                if (space == -1) {
                    officer.person.name.first = nameElement.getText()
                } else {
                    var first = nameElement.getText().substring(0, space)
                    var last = nameElement.getText().substring(space+1, nameElement.getText().length)
                    var fullname = "$first $last"

                    if (last == "") {
                        fullname = first
                    }

                    officer.person.name.first = first
                    officer.person.name.last = last
                    //nameElement.changePara(fullname)

                }
            }
        }

        var dismissButton = ConfirmCancelButton(Misc.getGrayColor(), element, 128f, 30f).apply {
            addText("Dismiss")
            centerText()
            blink = false
            position.belowLeft(nameElement.elementPanel, 20f)
        }

        dismissButton.onClick {
            dismissButton.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
        }

        element.addTooltip(dismissButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 300f) { tooltip ->
            tooltip.addPara("This officer can not be removed from the fleet.",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "")
        }

        var closeButton = ConfirmCancelButton(Misc.getGrayColor(), element, 128f, 30f).apply {
            addText("Close")
            centerText()
            blink = false
            position.belowLeft(dismissButton.elementPanel, 20f)
        }

        closeButton.onClick {
            closeButton.playClickSound()
            plugin.close()
        }

        plugin.onClose = {
            var slot = data.getOfficersAssignedSlot(officer!!)

            recreateAptitudeRow(subpanelParent, officer, slot!!)

        }

    }

}