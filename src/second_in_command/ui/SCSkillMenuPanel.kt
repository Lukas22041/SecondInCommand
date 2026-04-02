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
import second_in_command.ui.elements.*
import second_in_command.ui.panels.AssosciatesManagePanelPlugin
import second_in_command.ui.tooltips.OfficerTooltipCreator

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
        headerSubpanel!!.position.inTL(20f, 285+5f+if (isUseCompactLayout()) 5 else 15)


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
                tooltip.addPara("Executive officers provide fleetwide skills. They can be hired at the comm directory of colonies or found in derelict ships. ", 0f)
            }


            var otherSkillsButton = ClickableTextButton("Non-Executive Skills", Misc.getBasePlayerColor(), headerElement, 200f, 20f)
            otherSkillsButton.position.inTL(headerElement.widthSoFar/2-15, 1f)

            headerElement.addTooltip(otherSkillsButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 450f) { tooltip ->
                tooltip.addPara("Section for modded skills not managed by Second-in-Command which use the vanilla skill system. Skills displayed here can not be acquired through skill points, " +
                        "and can only be viewed, but some mods may provide separate ways to unlock some of those skills. ", 0f)
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

    fun isUseCompactLayout() : Boolean {
        return SCSettings.enableCompactLayout && SCSettings.playerOfficerSlots > 3
    }

    fun addAptitudePanel() {

        if (subpanel != null) {
            element.removeComponent(subpanel)
        }

        subpanel = Global.getSettings().createCustom(width, height, null)
        element.addComponent(subpanel)
        subpanel!!.position.inTL(20f, 285+5f+15)


        var scrollerPanel = Global.getSettings().createCustom(width - 20, if (isUseCompactLayout()) 420f else 400f, null)
        subpanel!!.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(0f, 25f)
        if (SCSettings.playerOfficerSlots > 3) scrollerPanel.position.inTL(-10f, if (isUseCompactLayout()) 10f else 25f)



        var subelement = scrollerPanel.createUIElement(width - 20,  if (isUseCompactLayout()) 420f else 400f, true)

        if (!title) {

            subelement.addSpacer(if (isUseCompactLayout()) 10f else 23f)

            /*addAptitudeRowParent(subelement, data.getOfficerInSlot(0), 0)

            subelement.addSpacer(30f)

            addAptitudeRowParent(subelement, data.getOfficerInSlot(1), 1)

            subelement.addSpacer(30f)

            addAptitudeRowParent(subelement, data.getOfficerInSlot(2), 2)

            if (SCSettings.enable4thSlot) {
                subelement.addSpacer(30f)

                addAptitudeRowParent(subelement, data.getOfficerInSlot(3), 3)

                subelement.addSpacer(23f)

                subelement.addLunaElement(0f, 0f).advance {
                    //lastAptitudeScrollerY = subelement.externalScroller.yOffset
                }
            }*/

            for (slot in 0 until SCSettings.playerOfficerSlots) {
                addAptitudeRowParent(subelement, data.getOfficerInSlot(slot), slot)
                var last = slot == SCSettings.playerOfficerSlots-1

                if (!last) {
                    subelement.addSpacer(30f)
                }
            }

            if (SCSettings.playerOfficerSlots > 3) {
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
        if (SCSettings.playerOfficerSlots > 3) {
            subelement.externalScroller.yOffset = lastAptitudeScrollerY
        }


    }

    fun addAptitudeRowParent(targetedElelement: TooltipMakerAPI, officer: SCOfficer?, slotId: Int) {
        var subpanel = Global.getSettings().createCustom(width, if (isUseCompactLayout()) 74f else 96f, null)
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

        var color = if (officer != null) officer.getAptitudePlugin().getColor() else Misc.getDarkPlayerColor()

        var isProgressionMode = SCSettings.progressionMode
        var level = Global.getSector().playerPerson.stats.level
        var progressionLevel = when(slotId) {
            0 -> SCSettings.progressionSlot1Level!!
            1 -> SCSettings.progressionSlot2Level!!
            2 -> SCSettings.progressionSlot3Level!!
            else -> SCSettings.progressionSlot3Level!! + (SCSettings.progressionModeLevelCurvePast3Slots * (slotId-2))
        }
        var isLocked = officer == null && isProgressionMode && level < progressionLevel
        var pickerElementSize = if (isUseCompactLayout()) 86f else 96f

        var menu = this

        // ── Empty / null-officer slot ───────────────────────────────────────────────────
        if (officer == null) {
            var officerPickerElement = SCOfficerPickerElement(null, color, subelement, pickerElementSize, pickerElementSize)
            officerPickerElement.isProgressionLocked = isLocked

            officerPickerElement.onClick {
                if (officerPickerElement.isInEditMode) return@onClick
                if (it.isRMBEvent) return@onClick
                if (isLocked) {
                    officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                    return@onClick
                }
                var pickerMenu = SCOfficerPickerMenuPanel(menu, officerPickerElement, subpanelParent, slotId, data, isAtColony)
                pickerMenu.init()
                officerPickerElement.playClickSound()
            }

            officerPickerElement.onHoverEnter {
                if (!officerPickerElement.isInEditMode) officerPickerElement.playScrollSound()
            }

            if (!isLocked) {
                subelement.addTooltipTo(OfficerTooltipCreator(null, isAtColony, false), officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)
            } else {
                subelement.addTooltip(officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 350f) { tooltip ->
                    tooltip.addPara("This slot is locked until the player has reached level $progressionLevel and can not be used until then.", 0f,
                        Misc.getTextColor(), Misc.getHighlightColor(), "$progressionLevel")
                }
            }

            var offsetElement = subelement.addLunaElement(0f, 0f)
            offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)
            var background = AptitudeBackgroundElement(color, subelement)
            background.elementPanel.position.belowLeft(offsetElement.elementPanel, 10f)
            var officerUnderline = SkillUnderlineElement(color, 2f, subelement, officerPickerElement.width)
            officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)
            return
        }

        // ── Officer present ─────────────────────────────────────────────────────────────
        val row = OfficerAptitudeRowElement(
            officer = officer,
            data = data,
            parentElement = subelement,
            officerSize = pickerElementSize,
            openedFromPicker = false,
            showCategory = false,
            showNameLabel = !isUseCompactLayout(),
            allowSkillStateChange = true,
            addLeadingSpacer = false
        )

        row.officerPickerElement.isProgressionLocked = isLocked

        row.officerPickerElement.onClick {
            if (row.officerPickerElement.isInEditMode) return@onClick

            if (SCUtils.isAssociatesBackgroundActive()) {
                openResrictedOfficerManagementPanel(panel, subpanelParent, officer)
                row.officerPickerElement.playClickSound()
                return@onClick
            }

            if (it.isRMBEvent) {
                if (data.getOfficerInSlot(slotId) != null) {
                    row.officerPickerElement.playSound("ui_char_decrease_skill", 1f, 1f)
                    data.setOfficerInSlot(slotId, null)
                    checkToApplyCRPenalty()
                    recreateAptitudeRow(subpanelParent, null, slotId)
                }
                return@onClick
            }

            if (isLocked) {
                row.officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }

            var pickerMenu = SCOfficerPickerMenuPanel(menu, row.officerPickerElement, subpanelParent, slotId, data, isAtColony)
            pickerMenu.init()
            row.officerPickerElement.playClickSound()
        }

        row.officerPickerElement.onHoverEnter {
            if (!row.officerPickerElement.isInEditMode) row.officerPickerElement.playScrollSound()
        }

        row.officerPickerElement.onInput { events ->
            if (row.officerPickerElement.isHovering) {
                for (event in events!!) {
                    if (event.isConsumed) continue
                    if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_R) {
                        event.consume()
                        if (!row.officerPickerElement.isInEditMode) {
                            var active = officer.getActiveSkillPlugins().filter { it != officer.getAptitudePlugin().originSkillPlugin }
                            var count = active.count()
                            var storyPoints = Global.getSector().playerPerson.stats.storyPoints
                            if (count == 0 || storyPoints <= 1) {
                                row.officerPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                                return@onInput
                            }
                            for (skill in active) {
                                officer.skillPoints += 1
                                skill.onDeactivation(data)
                            }
                            officer.activeSkillIDs = mutableSetOf()
                            Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
                            row.officerPickerElement.playSound(Sounds.STORY_POINT_SPEND)
                            Global.getSector().playerPerson.stats.storyPoints -= 2
                            recreateAptitudeRow(subpanelParent, officer, slotId)
                        }
                        break
                    }
                }
            }
        }

        if (!isLocked) {
            subelement.addTooltipTo(OfficerTooltipCreator(officer, isAtColony, false), row.officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)
        } else {
            subelement.addTooltip(row.officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 350f) { tooltip ->
                tooltip.addPara("This slot is locked until the player has reached level $progressionLevel and can not be used until then.", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "$progressionLevel")
            }
        }

        // Skill click handler – enters edit mode
        val aptitudePlugin = officer.getAptitudePlugin()
        row.skillBar.onSkillClick = { skillElement ->
            recalculateSectionRequirements(officer, row.skillBar.sections, row.skillBar.skillElements)

            if (skillElement.canChangeState && !skillElement.preAcquired) {
                enterEditMode(subpanelParent, officer, row.officerPickerElement, row.skillBar.skillElements, slotId)
                if (!skillElement.activated) skillElement.playSound(skillElement.soundId)
                else skillElement.playSound("ui_char_decrease_skill")
                skillElement.activated = !skillElement.activated
            } else {
                skillElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
            }

            recalculateSectionRequirements(officer, row.skillBar.sections, row.skillBar.skillElements)

            if (officer.activeSkillIDs.count() == row.skillBar.sections.sumOf { it.activeSkillsInUI.count { it.activated } }) {
                exitEditMode(subpanelParent, officer, row.officerPickerElement, slotId)
            }
        }

        // Initial section requirements pass
        recalculateSectionRequirements(officer, row.skillBar.sections, row.skillBar.skillElements)

        // SP counter para
        var spRemaining = row.skillBar.calculateRemainingSP()
        val officerPara = subelement.addPara(
            (if (isUseCompactLayout()) "${aptitudePlugin.name} - " else "") + "${officer.person.nameString} - $spRemaining SP",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining"
        )
        var hlColor = if (spRemaining == 0) Misc.getGrayColor() else Misc.getHighlightColor()
        officerPara.setHighlight(aptitudePlugin.name, "$spRemaining")
        officerPara.setHighlightColors(aptitudePlugin.color, hlColor)
        officerPara.position.rightOfBottom(row.spParaAnchor.elementPanel, 0f)

        row.spParaAnchor.advance {
            spRemaining = row.skillBar.calculateRemainingSP()
            var hlColor = if (spRemaining == 0) Misc.getGrayColor() else Misc.getHighlightColor()
            officerPara.text = (if (isUseCompactLayout()) "${aptitudePlugin.name} - " else "") + "${officer.person.nameString} - $spRemaining SP"
            officerPara.setHighlight(aptitudePlugin.name, "$spRemaining")
            officerPara.setHighlightColors(aptitudePlugin.color, hlColor)
        }


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

        var confirmButton = ConfirmCancelButton(picker.color, picker.innerElement, if (isUseCompactLayout()) 76f else 86f, if (isUseCompactLayout()) 25f else 30f).apply {
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


        var cancelButton = ConfirmCancelButton(picker.color, picker.innerElement, if (isUseCompactLayout()) 76f else 86f, if (isUseCompactLayout()) 25f else 30f).apply {
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