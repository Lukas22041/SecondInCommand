package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaExtensions.addLunaTextfield
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCData
import second_in_command.misc.SCSettings
import second_in_command.misc.addNegativePara
import second_in_command.misc.addTooltip
import second_in_command.misc.getAndLoadSprite
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCCategorySpec
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import second_in_command.ui.panels.BackgroundPanelPlugin
import second_in_command.ui.panels.ManagePanelPlugin
import second_in_command.ui.tooltips.OfficerTooltipCreator
import second_in_command.ui.tooltips.SCSkillTooltipCreator

class SCOfficerPickerMenuPanel(var menu: SCSkillMenuPanel, var originalPickerElement: SCOfficerPickerElement, var subpanelParent: CustomPanelAPI, var slotId: Int, var data: SCData, var isAtColony: Boolean) {

    var activePanel: CustomPanelAPI? = null
    var activeElement: TooltipMakerAPI? = null

    var selectedOfficer: SCOfficer? = null

    var lastScroller = 0f

    companion object {
        fun openPortraitPicker(officer: PersonAPI, menu: SCSkillMenuPanel) {
            var plugin = BackgroundPanelPlugin(menu.panel)

            var width = 530f
            var height = 500f

            var portraitPanel = menu.panel.createCustomPanel(width, height, plugin)
            plugin.panel = portraitPanel
            menu.panel.addComponent(portraitPanel)
            portraitPanel.position.inMid()

            var element = portraitPanel!!.createUIElement(width, height, true)
            element.position.inTL(0f, 0f)

            var lastElement: UIPanelAPI? = null
            var lastRowElement: UIPanelAPI? = null
            var elementPerRow = 5
            var currentCount = 0
            var size = 96f

            var portraits = ArrayList<String>()
            portraits += Global.getSector().playerFaction.factionSpec.getAllPortraits(FullName.Gender.MALE)
            portraits += Global.getSector().playerFaction.factionSpec.getAllPortraits(FullName.Gender.FEMALE)
            portraits = portraits.distinct() as ArrayList<String>

            for (portrait in portraits) {

                var luna = element.addLunaSpriteElement(portrait, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, size, 0f).apply {
                    enableTransparency = true
                    width = 0f
                    height = 0f
                    getSprite().alphaMult = 0.6f

                    advance {
                        if (isHovering) {

                            getSprite().alphaMult = 1f
                        }
                        else {

                            getSprite().alphaMult = 0.7f
                        }
                    }

                    onClick {
                        plugin.close()
                        officer.portraitSprite = portrait
                        playClickSound()
                    }

                    onHoverEnter {
                        playScrollSound()
                    }
                }

                luna.position.setSize(size, size)
                luna.getSprite().setSize(size, size)

                if (currentCount == 0) {
                    element.addSpacer(size + 10f)
                    if (lastRowElement != null) {
                        luna.elementPanel.position.belowLeft(lastRowElement, 10f)
                    }
                    lastRowElement = luna.elementPanel
                }
                else {
                    luna.elementPanel.position.rightOfMid(lastElement!!, 10f)
                }

                currentCount++

                if (currentCount == elementPerRow) {
                    currentCount = 0
                }

                lastElement = luna.elementPanel
            }

            portraitPanel.addUIElement(element)

        }
    }

    fun init() {
        recreatePanel()

    }

    fun recreatePanel() {

        if (activePanel != null) {
            if (activeElement != null) {
                lastScroller = activeElement!!.externalScroller.yOffset
            }

            menu.panel.removeComponent(activePanel)
        }

        var plugin = BackgroundPanelPlugin(menu.panel)

        var width = menu.width - 25
        var height = menu.height - 25

        var heightCap = 70f

        var popupPanel = menu.panel.createCustomPanel(width, height, plugin)
        plugin.panel = popupPanel
        menu.panel.addComponent(popupPanel)
        popupPanel.position.inMid()

        activePanel = popupPanel

        var scrollerPanel = popupPanel.createCustomPanel(width, height - heightCap, null)
        popupPanel.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(0f, 0f)
        var scrollerElement = scrollerPanel.createUIElement(width, height - heightCap, true)

        activeElement = scrollerElement

        //var officers = data.getOfficersInFleet().sortedByDescending { it.isAssigned() }
        var officers = data.getOfficersInFleet().sortedWith(compareBy({ !it.isAssigned() }, { it.getAptitudeSpec().order }))
        var activeOfficers = data.getAssignedOfficers()

        for (officer in officers) {

            var aptitudeSpec = SCSpecStore.getAptitudeSpec(officer.aptitudeId)
            var aptitudePlugin = aptitudeSpec!!.getPlugin()

            var categories = aptitudePlugin.categories

            var extra = 0f
            if (categories.isNotEmpty()) extra += 20f
            scrollerElement.addSpacer(10f)
            var officerElement = scrollerElement.addLunaElement(width - 10, 96f + 36 + extra).apply {
                enableTransparency = true
                backgroundAlpha = 0.025f
                borderAlpha = 0.1f
                backgroundColor = aptitudePlugin.getColor()
                borderColor = aptitudePlugin.getColor()
            }

            officerElement.advance {
                if (officer == selectedOfficer) {
                    officerElement.backgroundAlpha = 0.15f
                    officerElement.borderAlpha = 0.35f
                }
                else if (officerElement.isHovering) {
                    officerElement.backgroundAlpha = 0.1f
                    officerElement.borderAlpha = 0.25f
                }
                else {
                    officerElement.backgroundAlpha = 0.025f
                    officerElement.borderAlpha = 0.1f
                }
            }

            officerElement.onClick { selectOfficer(officer) }

            var inner = officerElement.innerElement

            val row = OfficerAptitudeRowElement(
                officer = officer,
                data = data,
                parentElement = inner,
                officerSize = 96f,
                openedFromPicker = true,
                showCategory = true,
                showNameLabel = true
            )

            row.officerPickerElement.onClick { selectOfficer(officer) }
            row.skillBar.originSkillElement.onClick { selectOfficer(officer) }
            row.skillBar.onSkillClick = { _ -> selectOfficer(officer) }
            row.categoryBackground?.onClick { selectOfficer(officer) }

            scrollerElement.addTooltipTo(OfficerTooltipCreator(officer, isAtColony, true), row.officerPickerElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)

            //Top Para
            val spRemaining = menu.calculateRemainingSP(officer, row.skillBar.skillElements)
            val spHighlight = if (spRemaining <= 0) Misc.getGrayColor() else Misc.getHighlightColor()

            var officerParaTextExtra = ""
            var minusText = ""
            if (officerAlreadySlotted(officer)) officerParaTextExtra = "This officer is already assigned."
            else if (doesOffficerMatchExistingAptitude(officer)) officerParaTextExtra = "Can't assign multiple officers of the same aptitude."
            else if (doesOffficerMatchCategory(officer)) officerParaTextExtra = "Can't assign multiple officers that are part of the same category."
            if (officerParaTextExtra != "") minusText = "-"

            val officerPara = inner.addPara("${officer.person.nameString} - $spRemaining SP $minusText $officerParaTextExtra", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$spRemaining")
            officerPara.position.rightOfBottom(row.spParaAnchor.elementPanel, 0f)
            officerPara.setHighlight("$spRemaining", officerParaTextExtra)
            officerPara.setHighlightColors(spHighlight, Misc.getNegativeHighlightColor())

            row.skillBar.recalculateInitialSectionRequirements()

        }

        scrollerElement.addSpacer(10f)

        scrollerPanel.addUIElement(scrollerElement)

        var buttonPanel = popupPanel.createCustomPanel(width, heightCap, null)
        popupPanel.addComponent(buttonPanel)
        buttonPanel.position.belowLeft(scrollerPanel, 0f)

        var buttonElement = buttonPanel.createUIElement(width, height, false)
        buttonElement.position.inTL(0f, 0f)
        buttonPanel.addUIElement(buttonElement)
        buttonElement.addPara("", 0f)

        //Confirm
        var confirmButton = ConfirmCancelButton(Misc.getGrayColor(), buttonElement, 120f, 35f).apply {
            addText("Confirm")
            centerText()
            blink = false
            position.inTR(150f+130+35+2, 14f)
        }

        confirmButton.advance {
            if (selectedOfficer != null) {

                if (officerAlreadySlotted(selectedOfficer!!) || doesOffficerMatchExistingAptitude(selectedOfficer!!) || doesOffficerMatchCategory(selectedOfficer!!)) {

                    confirmButton.color = Misc.getGrayColor()
                    confirmButton.blink = false

                    return@advance
                }

                var plugin = SCSpecStore.getAptitudeSpec(selectedOfficer!!.aptitudeId)!!.getPlugin()
                confirmButton.color = plugin.getColor()
                confirmButton.blink = true
            }
        }

        confirmButton.onClick {

            if (selectedOfficer == null || (officerAlreadySlotted(selectedOfficer!!) || doesOffficerMatchExistingAptitude(selectedOfficer!!) || doesOffficerMatchCategory(selectedOfficer!!))) {
                Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
                return@onClick
            }

            confirmButton.playClickSound()
            menu.panel.removeComponent(popupPanel)

            menu.checkToApplyCRPenalty()

            var previousOfficerInSlot = data.getOfficerInSlot(slotId)
            data.setOfficerInSlot(slotId, selectedOfficer!!)

            menu.recreateAptitudeRow(subpanelParent, data.getOfficerInSlot(slotId), slotId)
        }

        //Manage
        var manageButton = ConfirmCancelButton(Misc.getGrayColor(), buttonElement, 120f, 35f).apply {

            addText("Manage")
            centerText()
            blink = false
            position.rightOfTop(confirmButton.elementPanel, 10f)
        }

        manageButton.advance {
            if (selectedOfficer != null) {

                var aptitude = selectedOfficer!!.getAptitudeSpec()
                var plugin = aptitude.getPlugin()
                if (aptitude.tags.contains("unmanageable")) {
                    manageButton.color = Misc.getGrayColor()
                    manageButton.blink = false
                } else {
                    var plugin = SCSpecStore.getAptitudeSpec(selectedOfficer!!.aptitudeId)!!.getPlugin()
                    manageButton.color = plugin.getColor()
                    manageButton.blink = true
                }

            }
        }

        manageButton.onClick {

            if (selectedOfficer == null) {
                Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
                return@onClick
            }

            var aptitude = selectedOfficer!!.getAptitudeSpec()
            if (aptitude.tags.contains("unmanageable")) {
                Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
                return@onClick
            }

            manageButton.playClickSound()
            var plugin = openOfficerManagementPanel(popupPanel, selectedOfficer!!)
            plugin.onClose = {
                if (selectedOfficer != null) {
                    var slot = data.getOfficersAssignedSlot(selectedOfficer!!)
                    if (slot == slotId) {
                        menu.recreateAptitudeRow(subpanelParent, selectedOfficer, slot)
                    }
                    /*else if (slot != null) {
                        menu.recreateAptitudeRow(subpanelParent, selectedOfficer, slot)
                    }*/
                } /*else {
                    //Clear the slot if the officer was dismissed
                    menu.recreateAptitudeRow(subpanelParent, null, slotId)
                }*/

                //Clear Slots of dismissed officers
                for (i in 0 ..2) {
                    if (!data.getOfficersInFleet().contains(data.getOfficerInSlot(i))) {
                        menu.recreateAptitudeRow(menu.rowParents.get(i)!!, null, i)
                    }
                }

            }

        }

        buttonElement.addTooltip(manageButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 250f) { tooltip ->
            tooltip.addPara("Change the name or dismiss an officer, or change their portrait. ", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "name", "dismiss", "portrait")

            if (selectedOfficer != null) {
                var aptitude = selectedOfficer!!.getAptitudeSpec()
                if (aptitude.tags.contains("unmanageable")) {
                    tooltip.addSpacer(10f)
                    tooltip.addNegativePara("This officer can not be managed")
                }
            }

        }

        //Cancel
        var cancelButton = ConfirmCancelButton(Misc.getBasePlayerColor(), buttonElement, 120f, 35f).apply {
            addText("Cancel")
            centerText()
            blink = false
            //position.rightOfTop(confirmButton.elementPanel, 10f)
            position.rightOfTop(manageButton.elementPanel, 10f)
        }

        cancelButton.onClick {
            cancelButton.playClickSound()
            menu.panel.removeComponent(popupPanel)
        }


        scrollerElement.externalScroller.yOffset = lastScroller


         var helpIcon = HelpIconElement(Misc.getBasePlayerColor(), buttonElement, 35f, 35f)
         helpIcon.elementPanel.position.rightOfMid(cancelButton.elementPanel, 6f)

            buttonElement.addTooltip(helpIcon.elementPanel, TooltipMakerAPI.TooltipLocation.ABOVE, 400f) { tooltip ->
                tooltip.addPara("This screen can be used to assign, dismiss or re-name executive officers under your command. \n\n" +
                        "" +
                        "Executive Officers are a different class of officer than your standard ship piloting one. Unlike those, they specify in providing fleet-wide effects. Only 3 of them can be active at once. \n" + "\n" +
                        "You can occasionally find them on a colonies comm-directory, but they may also appear in other places, like cryo-pods on derelict ships. \n\n" +
                        "" +
                        "You can not have multiple officers of the same aptitude active at once. " +
                        "Additionally, some officers aptitudes are part of a category. Officers of the same category, for example \"Logistical\", can also not be used together. ",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "assign", "dismiss", "re-name", "Executive Officers","comm-directory", "cryo-pods",
                    "can not have multiple officers of the same aptitude active", "category", "Logistical")
            }
    }

    fun openOfficerManagementPanel(popupPanel: CustomPanelAPI, officer: SCOfficer) : ManagePanelPlugin {
        var plugin = ManagePanelPlugin(popupPanel, this)

        var width = 316f
        var height = 170f

        var managementPanel = menu.panel.createCustomPanel(width, height, plugin)
        plugin.panel = managementPanel
        popupPanel.addComponent(managementPanel)
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
            openPortraitPicker(officer.person, menu)
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

        var dismissButton = ConfirmCancelButton(Misc.getNegativeHighlightColor(), element, 128f, 30f).apply {
            addText("Dismiss")
            centerText()
            blink = false
            position.belowLeft(nameElement.elementPanel, 20f)
        }

        dismissButton.onClick {
            dismissButton.playClickSound()
            if (it.isDoubleClick && it.isLMBDownEvent) {
                selectedOfficer = null
                dismissButton.playSound(Sounds.STORY_POINT_SPEND, 1f, 1f)
                data.removeOfficerFromFleet(officer)
                plugin.close()
            }
        }

        element.addTooltip(dismissButton.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 300f) { tooltip ->
            tooltip.addPara("Click this button twice in rapid succession to permanently dismiss this officer. Doing so will forever remove them from your fleet",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "twice", "permanently dismiss")
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

        return plugin

    }



    fun selectOfficer(officer: SCOfficer) {

        /*if (officerAlreadySlotted(officer) || doesOffficerMatchExistingAptitude(officer) || doesOffficerMatchCategory(officer)) {
            Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
            return
        }*/

        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
        selectedOfficer = officer
    }


    fun doesOffficerMatchExistingAptitude(officer: SCOfficer) : Boolean {

        for (active in data.getAssignedOfficers()) {
            if (active == null) continue
            if (active.person == originalPickerElement.officer) continue
            if (active.aptitudeId == officer.aptitudeId) return true
        }

        return false
    }



    fun doesOffficerMatchCategory(officer: SCOfficer) : Boolean {
        if (SCSettings.disableCategoryRestrictions) return false
        var list = mutableListOf<SCCategorySpec>()
        var categories = officer.getAptitudePlugin().categories
        for (active in data.getAssignedOfficers()) {
            if (active == null) continue
            if (active.person == originalPickerElement.officer) continue

            var othersCategories = active.getAptitudePlugin().categories

            if (othersCategories.any { categories.contains(it) }) {
                return true
            }
        }

        return false
    }

    fun officerAlreadySlotted(officer: SCOfficer) : Boolean {
        return data.getAssignedOfficers().contains(officer)
    }

    fun getActiveSkillCount(sections: ArrayList<SCAptitudeSection>) : Int {
        return sections.sumOf { it.activeSkillsInUI.count { it.activated } }
    }

    fun calculateSectionRequirements(officer: SCOfficer, sections: MutableList<SCAptitudeSection>, skillElements: ArrayList<SkillWidgetElement>) {
        for (section in sections) {

            var count = getActiveSkillCount(section.previousUISections)

            section.uiGap?.renderArrow = section.requiredPreviousSkills <= count
            section.tooltips.forEach { it.sectionMeetsRequirements = section.requiredPreviousSkills <= count }

        }
    }

}


