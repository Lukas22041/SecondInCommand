package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCData
import second_in_command.misc.clearChildren
import second_in_command.ui.elements.*

class SCPlayerPanel(var menu: SCSkillMenuPanel, var data: SCData)  {


    fun init() {

        var width = menu.width
        var height = menu.height

        var subpanel = Global.getSettings().createCustom(width, height, null)
        menu.element.addCustom(subpanel, 0f)
        subpanel.position.inTL(15f, 25f)

        recreatePanel(subpanel)
    }

    fun recreatePanel(subpanel: CustomPanelAPI) {
        subpanel.clearChildren()

        var width = menu.width
        var height = menu.height

        var player = Global.getSector().playerPerson
        var subelement = subpanel.createUIElement(width, height, false)
        subpanel.addUIElement(subelement)

        subelement.addSpacer(5f)

        //Name Changing
        var nameElement = BackgroundlessTextfield(player.nameString, Misc.getBasePlayerColor(), subelement, 250f, 30f)
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
        }, nameElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW )


        var portrait = LunaSpriteElement(player.portraitSprite, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, subelement, 128f, 128f)
        portrait.elementPanel.position.belowLeft(nameElement.elementPanel, 15f)

        var placeholder = LunaElement(subelement, 0f, 0f)
        placeholder.elementPanel.position.rightOfMid(portrait.elementPanel, 30f)


        //Skillpoints
        var skillBox = SkillPointsBox(subelement, 100f, 50f)
        skillBox.elementPanel.position.aboveLeft(placeholder.elementPanel, 4f)

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                var plugin = Global.getSettings().levelupPlugin

                var maxLevel = plugin.maxLevel
                var maxSkillPoints = 0
                for (i in 1 .. plugin.maxLevel) {
                    maxSkillPoints += plugin.getPointsAtLevel(i)
                }

                tooltip!!.addPara("Learning a skill requires one skill point. The players and their officers skill points are separate. " +
                        "This number shows the skill points the player has.", 0f, Misc.getTextColor(), Misc.getHighlightColor())

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



        //XPBar
        var xpBar = PlayerXPBarElement(subelement, 260f, 20f)
        xpBar.position.belowLeft(portrait.elementPanel, 10f)

        subelement.addSpacer(5f)
        var level = Global.getSector().playerPerson.stats.level
        var levelText = "   - Level $level"
        if (level >= Global.getSettings().levelupPlugin.maxLevel) levelText += " (maximum)"
        var levelPara = subelement.addPara("$levelText", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "$level")

    }



}