package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaExtensions.addLunaElement
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.FullDialogAptitudeBackgroundElement
import second_in_command.ui.elements.SkillSeperatorElement
import second_in_command.ui.elements.SkillWidgetElement
import second_in_command.ui.tooltips.SCSkillTooltipCreator

object SCUtils {

    var MOD_ID = "second_in_command"
    var DATA_KEY = "\$sc_stored_data"

    @JvmStatic
    fun getPlayerData() : SCData {
        var data = Global.getSector().playerFleet.memoryWithoutUpdate.get(DATA_KEY) as SCData?
        if (data == null) {
            data = SCData(Global.getSector().playerFleet)
            Global.getSector().playerFleet.memoryWithoutUpdate.set(DATA_KEY, data)
            data!!.init()
        }
        return data
    }


    fun getFleetData(fleet: CampaignFleetAPI) : SCData{
        var data = fleet.memoryWithoutUpdate.get(DATA_KEY) as SCData?
        if (data == null) {
            data = SCData(fleet)
            fleet.memoryWithoutUpdate.set(DATA_KEY, data)
            data!!.init() //Move init to after the data has been assigned to the fleet key, otherwise it can cause some infinite loops
        }
        return data
    }

    fun hasFleetData(fleet: CampaignFleetAPI) : Boolean {
        return fleet.memoryWithoutUpdate.get(DATA_KEY) as SCData? != null
    }

    @JvmStatic
    fun createRandomSCOfficer(aptitudeId: String) : SCOfficer {
        var person = Global.getSector().playerFaction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }

    @JvmStatic
    fun createRandomSCOfficer(aptitudeId: String, faction: FactionAPI) : SCOfficer {
        var person = faction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }

    /*@JvmStatic
    fun isSkillActive(skillId: String) : Boolean {
        return getPlayerData().isSkillActive(skillId)
    }*/



    fun ShipVariantAPI.addAndCheckTag(tag: String) : Boolean {
        if (this.hasTag(tag)) return true

        this.addTag(tag)
        return false
    }

    fun CampaignFleetAPI.addAndCheckTag(tag: String) : Boolean {
        if (this.hasTag(tag)) return true

        this.addTag(tag)
        return false
    }


    fun changeOfficerAptitude(fleet: CampaignFleetAPI, officer: SCOfficer, aptitudeId: String) {
        var data = getFleetData(fleet)

        if (officer.isAssigned()) {
            var skills = officer.getActiveSkillPlugins()
            for (skill in skills) {
                skill.onDeactivation(data)
            }
        }
        officer.activeSkillIDs.clear()
        officer.resetLevel()
        officer.aptitudeId = aptitudeId

        if (officer.isAssigned()) {
            var skills = officer.getActiveSkillPlugins()
            for (skill in skills) {
                skill.onActivation(data)
            }
        }
    }

    @JvmStatic
    fun computeThresholdBonus(current: Float, maxBonus: Float, maxThreshold: Float): Float {

        var bonus = 0f
        var currValue = current
        var threshold = maxThreshold

        bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold)
        return bonus
    }

    @JvmStatic
    private fun getThresholdBasedRoundedBonus(maxBonus: Float, value: Float, threshold: Float): Float {
        var bonus = maxBonus * threshold / Math.max(value, threshold)
        if (bonus > 0 && bonus < 1) bonus = 1f
        if (maxBonus > 1f) {
            if (bonus < maxBonus) {
                bonus = Math.min(bonus, maxBonus - 1f)
            }
            bonus = Math.round(bonus).toFloat()
        }
        return bonus
    }


    @JvmStatic
    fun showSkillOverview(dialog: InteractionDialogAPI, officer: SCOfficer) {
        var textPanel = dialog.textPanel
        var tooltip = textPanel.beginTooltip()

        tooltip.addSpacer(20f)

        var data = SCUtils.getPlayerData()

        var width = 500f
        var height = 44f
        var panel = Global.getSettings().createCustom(width, height, null)
        tooltip.addCustom(panel, 0f)
        var element = panel.createUIElement(width, height, false)
        panel.addUIElement(element)

        var aptitudePlugin = officer.getAptitudePlugin()

        aptitudePlugin.clearSections()
        aptitudePlugin.createSections()
        var skills = mutableListOf<SCBaseSkillPlugin>()
        skills.add(aptitudePlugin.getOriginSkillPlugin())
        skills.addAll(aptitudePlugin.getSections().flatMap { it.getSkills() }.map { SCSpecStore.getSkillSpec(it)!!.getPlugin() })

        var background = FullDialogAptitudeBackgroundElement(aptitudePlugin.getColor(), element, 8f)
        background.elementPanel.position.inTL(0f, 20f)

        var first: CustomPanelAPI? = null
        var previous: CustomPanelAPI = background.elementPanel
        for (skill in skills) {
            var isFirst = skills.first() == skill
            var isLast = skills.last() == skill

            var skillElement = SkillWidgetElement(skill.getId(), true, false, true, skill.getIconPath(), "", aptitudePlugin.getColor(), element, 40f, 40f)

            skillElement.onClick {
                skillElement.playClickSound()
            }

            var tooltip = SCSkillTooltipCreator(data, skill, aptitudePlugin, 0, false)
            element.addTooltipTo(tooltip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)

            if (previous != background.elementPanel) {
                skillElement.elementPanel.position.rightOfTop(previous, 1f)
            } else {
                first = skillElement.elementPanel
                skillElement.elementPanel.position.rightOfMid(previous, 3f)
            }


            if (!isLast) {
                var seperator = SkillSeperatorElement(aptitudePlugin.getColor(), element, 44f)
                seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 1f)
                previous = seperator.elementPanel
            }
        }

        var paraElement = element.addLunaElement(100f, 20f).apply {
            renderBorder = false
            renderBackground = false
        }
        paraElement.elementPanel.position.aboveLeft(first, 0f)

        paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = paraElement.innerElement.addPara(aptitudePlugin.getName(), 0f, aptitudePlugin.getColor(), aptitudePlugin.getColor())
        aptitudePara.position.inTL(0f, paraElement.height  -aptitudePara.computeTextHeight(aptitudePara.text)-5)


        textPanel.addTooltip()
    }
}