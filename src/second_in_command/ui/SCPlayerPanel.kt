package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCData
import second_in_command.misc.clearChildren
import second_in_command.ui.elements.*

class SCPlayerPanel(var menu: SCSkillMenuPanel, var data: SCData)  {

    var skillPoints = Global.getSector().playerPerson.stats.points

    fun init() {

        var width = menu.width
        var height = menu.height

        var subpanel = Global.getSettings().createCustom(width, height, null)
        menu.element.addCustom(subpanel, 0f)
        subpanel.position.inTL(15f, 30f)

        recreatePlayerPanel(subpanel)

    }

    fun recreateAptitudePanel(subpanel: CustomPanelAPI) {

        var width = menu.width
        var height = menu.height

        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)
        subelement.position.inTL(300f, 0f)

        subelement.addPara("Test",0f)
    }

    fun recreatePlayerPanel(subpanel: CustomPanelAPI) {
        subpanel.clearChildren()

        var width = menu.width
        var height = menu.height

        var player = Global.getSector().playerPerson
        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)

        subelement.addSpacer(5f)

        //Name Changing
        var nameElement = BackgroundlessTextfield(player.nameString, Misc.getBasePlayerColor(), subelement, 260f, 30f)
        nameElement.advance {


            var playerName = player.nameString
            if (playerName != nameElement.getText()) {
                var space = nameElement.getText().indexOf(" ")

                if (space == -1) {
                    player.name.first = nameElement.getText()
                } else {
                    var first = nameElement.getText().substring(0, space)
                    var last = nameElement.getText().substring(space+1, nameElement.getText().length)
                    var fullname = "$first $last"

                    if (last == "") {
                        fullname = first
                    }

                    player.name.first = first
                    player.name.last = last
                    //nameElement.changePara(fullname)

                }
            }
        }

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip!!.addPara("Click to adjust your name", 0f, Misc.getTextColor(), Misc.getHighlightColor())
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 175f
            }
        }, nameElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )


        var portrait = LunaSpriteElement(player.portraitSprite, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, subelement, 128f, 128f)
        portrait.elementPanel.position.belowLeft(nameElement.elementPanel, 15f)

        var placeholder = LunaElement(subelement, 0f, 0f)
        placeholder.elementPanel.position.rightOfMid(portrait.elementPanel, 30f)


        //Skillpoints
        var skillBox = SkillPointsBox(subelement, 100f, 50f)
        skillBox.elementPanel.position.aboveLeft(placeholder.elementPanel, 4f)

        skillBox.advance {
            skillBox.points = skillPoints
        }

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                var plugin = Global.getSettings().levelupPlugin

                var maxLevel = plugin.maxLevel
                var maxSkillPoints = 0
                for (i in 1 .. plugin.maxLevel) {
                    maxSkillPoints += plugin.getPointsAtLevel(i)
                }

                tooltip!!.addPara("Learning a skill requires one skill point. The player and their executive officers skill points are separate. " +
                        "This number shows the skill points the player has.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "one")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("You gain 1 skill point for every 2nd level. In total, $maxSkillPoints skill points will be available after you reach the maximum level of $maxLevel.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "1", "2nd", "$maxSkillPoints", "$maxLevel")
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 400f
            }
        }, skillBox.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )




        //Storypoints
        var storyBox = StoryPointsBox(subelement, 100f, 50f)
        storyBox.elementPanel.position.belowLeft(placeholder.elementPanel, 10f)

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                var plugin = Global.getSettings().levelupPlugin
                var spPerLevel = plugin.storyPointsPerLevel


                tooltip!!.addPara("Using story points enables you to take actions that are in some way exceptional.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "story points")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("The range of effects includes making permanent modifications to ships, officer customisation, and getting dialog options that are not otherwise available," +
                        " such as disengaging unscathed from battle you do not want.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("You gain $spPerLevel story points over the course of each level. In addition, you will continue to gain story points after reaching the maximum level.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "$spPerLevel")

                tooltip.addSpacer(10f)
                tooltip.addSectionHeading("Bonus Experience", Alignment.MID, 0f)
                tooltip.addSpacer(10f)

                tooltip!!.addPara("Some uses of story points grant bonus experience, which doubles your experience gain until it's used up.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "bonus experience")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("The less long-term or impactful the use, the more bonus experience it grants. 100%% bonus experience means \"enough to earn an extra story point\", " +
                        "eventually compensating for the use of the story point entirely.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "100%")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("Some of the bonus experience is granted immediately, while the remainder is gained on reaching the maximum level.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "")
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 400f
            }
        }, storyBox.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )


        //XPBar
        var xpBar = PlayerXPBarElement(subelement, 260f, 20f)
        xpBar.position.belowLeft(portrait.elementPanel, 10f)

        subelement.addSpacer(5f)
        var level = Global.getSector().playerPerson.stats.level
        var levelText = "   - Level $level"
        if (level >= Global.getSettings().levelupPlugin.maxLevel) levelText += " (maximum)"
        var levelPara = subelement.addPara("$levelText", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "$level")

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                var plugin = Global.getSettings().levelupPlugin
                var level = Global.getSector().playerPerson.stats.level
                var maxLevel = plugin.maxLevel
                var xp = Global.getSector().playerPerson.stats.xp
                var spPerLevel = plugin.storyPointsPerLevel
                var bonusXp = Global.getSector().playerPerson.stats.bonusXp
                var extraBonusXP = Global.getSector().playerPerson.stats.deferredBonusXp


                var xpInThisLevel = xp - plugin.getXPForLevel(level)
                var xpForThisLevel =  plugin.getXPForLevel(level+1) - plugin.getXPForLevel(level)

                var maxSkillPoints = 0
                for (i in 1 .. plugin.maxLevel) {
                    maxSkillPoints += plugin.getPointsAtLevel(i)
                }

                tooltip!!.addPara("Current level: $level, maximum is $maxLevel.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$level", "$maxLevel")

                tooltip.addSpacer(10f)

                var xpString = Misc.getWithDGS(xpInThisLevel.toFloat())
                var xpRequiredString = Misc.getWithDGS(xpForThisLevel.toFloat())

                tooltip!!.addPara("$xpString out of $xpRequiredString experience gained towards next level.", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "$xpString", "$xpRequiredString")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("You gain an additional skill point on every 2nd level.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "2nd")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("You gain $spPerLevel story points over the course of each level. In addition, you will continue to gain story points after reaching the maximum level.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "$spPerLevel")

                tooltip.addSpacer(10f)
                tooltip.addSectionHeading("Bonus Experience", Alignment.MID, 0f)
                tooltip.addSpacer(10f)

                var bonusXPString = Misc.getWithDGS(bonusXp.toFloat())

                tooltip!!.addPara("You have $bonusXPString bonus experience. Bonus experience doubles your experience until it's used up, and is acquired by using story points in certain ways.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "$bonusXPString")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("After reaching the maximum level, bonus experience quadruples your experience instead.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "quadruples")

                tooltip.addSpacer(10f)

                var extraBonusXPString = Misc.getWithDGS(extraBonusXP.toFloat())

                tooltip!!.addPara("You will gain $extraBonusXPString additional bonus experience on reaching the maximum level, based on your use of story points so far.",
                    0f, Misc.getTextColor(), Misc.getStoryOptionColor(), "$extraBonusXPString")
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 400f
            }
        }, xpBar.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )

        var line = subelement.addLunaElement(2f, 240f).apply {
            enableTransparency = true
            renderBorder = false
        }

        line.elementPanel.position.rightOfTop(nameElement.elementPanel, 20f)


        recreateAptitudePanel(subpanel)
    }



}