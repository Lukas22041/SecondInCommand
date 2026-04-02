package second_in_command.interactions.rules

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import second_in_command.SCUtils
import second_in_command.misc.addTooltip
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*

class SCStartBarEventDialog : BaseCommandPlugin() {
    override fun execute(ruleId: String?, dialog: InteractionDialogAPI, params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {
        dialog.optionPanel.clearOptions()

        var plugin = SCStartBarEventDialogDelegate(dialog.plugin)
        dialog.plugin = plugin
        plugin.init(dialog)

        return true
    }
}

class SCStartBarEventDialogDelegate(var original: InteractionDialogPlugin) : InteractionDialogPlugin {

    lateinit var dialog: InteractionDialogAPI
    lateinit var textPanel: TextPanelAPI
    lateinit var optionPanel: OptionPanelAPI

    var officers = ArrayList<SCOfficer>()

    override fun init(dialog: InteractionDialogAPI) {

        this.dialog = dialog
        this.textPanel = dialog.textPanel
        this.optionPanel = dialog.optionPanel

        var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }.filter { it.getTags().contains("startingOption") }
        for (aptitude in aptitudes) {
            var officer = SCUtils.createRandomSCOfficer(aptitude.getId(), dialog.interactionTarget.market.faction)
            officers.add(officer)
        }

        textPanel.addPara("You approach the person that appears to be trying to sell of an assortment of contracts, though so far it appears to be to no avail.")

        textPanel.addPara("\"Hey, you there. You appear to be just like the kind of aspiring captain that will once be a ruling force of the sector, or some more moderate goal if that is to your liking!")

        textPanel.addPara("However, such goals are never reached without some support. May you be interested in what our company has to offer?\". Despite his high energy, or possibly because of it, all he says appears rather desperate.")

        optionPanel.addOption("Hear him out on his offer", "HEAR_HIM_OUT")

        //optionPanel.addOption("Return", "RETURN")
    }

    fun returnToBar() {

        Global.getSector().memoryWithoutUpdate.set("\$sc_selectedStart", true)

        dialog.optionPanel.clearOptions()

        dialog.plugin = original
        dialog.visualPanel.hideFirstPerson()
        dialog.interactionTarget.activePerson = null
        (dialog.plugin as RuleBasedDialog).notifyActivePersonChanged()

        dialog.plugin.memoryMap.get(MemKeys.LOCAL)!!.set("\$option", "backToBar")
        FireAll.fire(null, dialog, memoryMap, "DialogOptionSelected")
    }

    fun chooseOfficer(officer: SCOfficer) {
        optionPanel.clearOptions()

        var data = SCUtils.getPlayerData()

        textPanel.addPara("> Choose an officer", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

        textPanel.addParagraph("${officer.person.nameString} (level ${officer.getCurrentLevel()}) has joined your fleet",  Misc.getPositiveHighlightColor())


        textPanel.addPara("\"Fantastic Decision! We hope that your officer of choice will be able to further assist you in your goals! We hope you will continue to make deals with us later!")

        textPanel.addPara("\"You can usually find available contracts from our company at the colonies comm-link in case you require more officers\". He waves you goodbye as he continues looking for another customer. ",
        Misc.getTextColor(), Misc.getHighlightColor(), "comm-link")

        textPanel.addPara("You can find out more about your new officer in the \"Character\" tab of your tri-pad.", Misc.getGrayColor(), Misc.getHighlightColor(), "Character")

        data.addOfficerToFleet(officer)
        data.setOfficerInEmptySlotIfAvailable(officer)


        optionPanel.addOption("Return to the bar", "LEAVE")
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        if (optionData == "HEAR_HIM_OUT") {
            optionPanel.clearOptions()

            textPanel.addPara("> Hear him out on his offer", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            textPanel.addPara("\"Great choice! Our company specializes in what you may call Executive Officers. " +
                    "This class of officer aims to support a fleet with their own unique kinds of skills. Every officer has a different aptitude that they exceed in.",
                Misc.getTextColor(), Misc.getHighlightColor(), "Executive Officers", "skills", "aptitude")

            textPanel.addPara("As part of a special promotion, the first contract signed through our company will be paid in full by ourself, see it as a sign of good-will for future deals!\". You continue to hear him out, and gain a potential picture of what may be driving their struggling business. " +
                    "He continues \"Right here i've got a list of available offers, feel free to choose one of them!\"")

            var tooltip = textPanel.beginTooltip()

            tooltip.addPara("The list of officers is separated in to \"Recommended\" and \"Advanced\" officers. Both may be viable for your fleet, but the recommended selection will be applicable to most fleets. ",
                0f, Misc.getGrayColor(), Misc.getHighlightColor(), "\"Recommended\"", "\"Advanced\"")

            textPanel.addTooltip()

            optionPanel.addOption("Choose an officer", "ACCEPT")
            optionPanel.addOption("Decline his offer", "DECLINE")

        }

        if (optionData == "ACCEPT") {
            var width = 1080f
            dialog.showCustomVisualDialog(width, 700f, SCBarDelegatePanel(this, officers, width))
        }

        if (optionData == "DECLINE") {

            optionPanel.clearOptions()

            textPanel.addPara("> Decline his offer", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            textPanel.addPara("\"That's a shame...however we hope that if you become interested, you come in to contact with us again\". As he continues you can hear him just barely hold it together." +
                    "")

            textPanel.addPara("\"You can usually find available contracts from the colonies comm-link\". He waves you goodbye as he continues looking for another customer. ", Misc.getTextColor(), Misc.getHighlightColor(), "comm-link")

            optionPanel.addOption("Return to the bar", "LEAVE")
        }

        if (optionData == "LEAVE") {
            textPanel.addPara("> Return to the bar", Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

            returnToBar()
        }

    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI> {
        return original.memoryMap
    }
}

class SCBarDelegatePanel(var plugin: SCStartBarEventDialogDelegate, var officers: List<SCOfficer>, var width: Float) : CustomVisualDialogDelegate {

    var selectedOfficer: SCOfficer? = null


    override fun init(panel: CustomPanelAPI?, callbacks: CustomVisualDialogDelegate.DialogCallbacks?) {


        var height = panel!!.position.height - 25

        var heightCap = 40f

        var scrollerPanel = panel.createCustomPanel(width, height - heightCap, null)
        panel.addComponent(scrollerPanel)
        scrollerPanel.position.inTL(0f, 0f)
        var scrollerElement = scrollerPanel.createUIElement(width, height - heightCap, true)

        var data = SCUtils.getPlayerData()

        var recommended = mutableListOf(
            "sc_tactical",
            "sc_management",
            "sc_engineering",
            "sc_starfaring",
        )

        //Recommended Header
        scrollerElement.addSpacer(10f)
        var recommendedHeader = scrollerElement.addSectionHeading("Recommended by the Seller", Alignment.MID, 0f)
        recommendedHeader.position.setXAlignOffset(10f)
        recommendedHeader.position.setSize(recommendedHeader.position.width - 25, recommendedHeader.position.height)

        var officers = officers.sortedWith(compareBy({ !recommended.contains(it.aptitudeId ) }, { !it.isAssigned() }, { it.getAptitudeSpec().order }))
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



            officerElement.onClick {
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                selectedOfficer = officer
            }


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

            row.officerPickerElement.onClick {
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                selectedOfficer = officer
            }
            row.skillBar.originSkillElement.onClick {
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                selectedOfficer = officer
            }
            row.skillBar.onSkillClick = { _ ->
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                selectedOfficer = officer
            }
            row.categoryBackground?.onClick {
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
                selectedOfficer = officer
            }
            row.skillBar.recalculateInitialSectionRequirements()

            //Para
            val officerPara = inner.addPara("${officer.person.nameString} - 1 SP", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "1")
            officerPara.position.rightOfBottom(row.spParaAnchor.elementPanel, 0f)

            //Non-Recommmended Header
            if (recommended.contains(officer.aptitudeId)) {
                recommended.remove(officer.aptitudeId)
                if (recommended.isEmpty()) {
                    //Recommended Header
                    scrollerElement.addSpacer(10f)
                    var nonRecommendedHeader = scrollerElement.addSectionHeading("Advanced & Specialized Officers", Alignment.MID, 0f)
                    nonRecommendedHeader.position.setXAlignOffset(10f)
                    nonRecommendedHeader.position.setSize(nonRecommendedHeader.position.width - 25, nonRecommendedHeader.position.height)
                }
            }

        }

        scrollerElement.addSpacer(10f)

        scrollerPanel.addUIElement(scrollerElement)


        var buttonPanel = panel.createCustomPanel(width, heightCap, null)
        panel.addComponent(buttonPanel)
        buttonPanel.position.belowLeft(scrollerPanel, 0f)

        var buttonElement = buttonPanel.createUIElement(width, height, false)
        buttonElement.position.inTL(0f, 0f)
        buttonPanel.addUIElement(buttonElement)
        buttonElement.addPara("", 0f)

        var confirmButton = ConfirmCancelButton(Misc.getGrayColor(), buttonElement, 120f, 35f).apply {
            addText("Confirm")
            centerText()
            blink = false
            position.inTR(150f + 35, 14f)
        }

        confirmButton.advance {
            if (selectedOfficer != null) {
                var plugin = SCSpecStore.getAptitudeSpec(selectedOfficer!!.aptitudeId)!!.getPlugin()
                confirmButton.color = plugin.getColor()
                confirmButton.blink = true
            }
        }

        confirmButton.onClick {

            if (selectedOfficer == null) {
                Global.getSoundPlayer().playUISound("ui_button_disabled_pressed", 1f, 1f)
                return@onClick
            }

            confirmButton.playSound(Sounds.STORY_POINT_SPEND, 1f, 1f)
            callbacks!!.dismissDialog()
            plugin.chooseOfficer(selectedOfficer!!)

        }

        var cancelButton = ConfirmCancelButton(Misc.getBasePlayerColor(), buttonElement, 120f, 35f).apply {
            addText("Cancel")
            centerText()
            blink = false
            position.rightOfTop(confirmButton.elementPanel, 10f)
        }

        cancelButton.onClick {
            cancelButton.playClickSound()
            callbacks!!.dismissDialog()
        }

        var helpIcon = HelpIconElement(Misc.getBasePlayerColor(), buttonElement, 35f, 35f)
        helpIcon.elementPanel.position.rightOfMid(cancelButton.elementPanel, 6f)

        buttonElement.addTooltip(helpIcon.elementPanel, TooltipMakerAPI.TooltipLocation.ABOVE, 400f) { tooltip ->
            tooltip.addPara("Executive Officers are a different class of officer than your standard ship piloting one. Unlike those, they specify in providing fleet-wide effects. Only 3 of them can be active at once. \n\n" +
                    "You can occasionally find them on a colonies comm-directory, but they may also appear in other places, like cryo-pods on derelict ships. \n\n" +
                    "" +
                    "You can not have multiple officers of the same aptitude active at once. " +
                    "Additionally, some officers aptitudes are part of a category. Officers of the same category, for example \"Logistical\", can also not be used together. ",
                0f, Misc.getTextColor(), Misc.getHighlightColor(), "Executive Officers", "comm-directory", "cryo-pods",
                "can not have multiple officers of the same aptitude active", "category", "Logistical")
        }

    }

    override fun getCustomPanelPlugin(): CustomUIPanelPlugin? {
        return null
    }

    override fun getNoiseAlpha(): Float {
        return 0f
    }

    override fun advance(amount: Float) {

    }

    override fun reportDismissed(option: Int) {

    }


}