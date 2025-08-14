package second_in_command.ui

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Sounds
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.ui.impl.StandardTooltipV2
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import org.lwjgl.input.Keyboard
import second_in_command.SCData
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.VanillaSkillTooltip
import second_in_command.misc.clearChildren
import second_in_command.misc.loadTextureCached
import second_in_command.skills.PlayerLevelEffects
import second_in_command.specs.SCAptitudeSection
import second_in_command.ui.elements.*

class SCPlayerPanel(var menu: SCSkillMenuPanel, var data: SCData)  {

    var skillPoints = Global.getSector().playerPerson.stats.points
    var startSkillPoints = Global.getSector().playerPerson.stats.points

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


        var acquiredSkillsIds = Global.getSector().playerPerson.stats.skillsCopy.filter { it.level >= 2 }.map { it.skill.id }

        var player = Global.getSector().playerPerson
        var color = Global.getSettings().getSkillSpec("aptitude_combat").governingAptitudeColor
        var sections = ArrayList<SCAptitudeSection>()

        var skills = ArrayList<String>()
        skills.add("helmsmanship")
        skills.add("combat_endurance")
        skills.add("impact_mitigation")
        skills.add("damage_control")
        skills.add("field_modulation")
        skills.add("target_analysis")
        skills.add("systems_expertise")

        skills.add("point_defense")
        skills.add("energy_weapon_mastery")
        skills.add("ballistic_mastery")
        skills.add("gunnery_implants")
        skills.add("ordnance_expert")
        skills.add("polarized_armor")
        skills.add("missile_specialization")

        var background = PlayerAptitudeBackgroundElement(color, subelement)
        background.elementPanel.position.inTL(10f, 12f)

        //Aptitude Icon
        var path = "graphics/secondInCommand/combat_icon.png"
        Global.getSettings().loadTextureCached(path)
        var aptitudeIconElement = CombatSkillWidgetElement("helmsmanship", true, false, true, path, "combat2", color, subelement, 96f, 96f)

        //var combatIcon = subelement.addLunaSpriteElement("graphics/secondInCommand/combat_icon.png", LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, 110f, 110f)
        aptitudeIconElement.elementPanel.position.inTL(28f, 76f)

        aptitudeIconElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
        var aptitudePara = aptitudeIconElement.innerElement.addPara("Combat", 0f, color, color)
        aptitudePara.position.inTL(aptitudeIconElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 , -aptitudePara.computeTextHeight(aptitudePara.text)-5)

        var combatSkillUnderline = SkillUnderlineElement(color, 2f, subelement, 96f)
        combatSkillUnderline.position.belowLeft(aptitudeIconElement.elementPanel, 2f)

        subelement.addTooltipTo(object : BaseTooltipCreator() {
            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                var plugin = Global.getSettings().levelupPlugin
                var maxLevel = plugin.maxLevel
                var maxSkillPoints = 0
                var storyPoins = Global.getSector().playerPerson.stats.storyPoints
                for (i in 1 .. plugin.maxLevel) {
                    maxSkillPoints += plugin.getPointsAtLevel(i)
                }

                tooltip!!.addPara("This section of skills are the players own set of in-combat skills. You can use your own skill points to acquire them in any order. All player skills are elite by default.", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "skill points", "in any order", "elite")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("You gain 1 skill point for every 2nd level. In total, $maxSkillPoints skill points will be available after you reach the maximum level of $maxLevel.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(), "1", "2nd", "$maxSkillPoints", "$maxLevel")

                tooltip.addSpacer(10f)

                tooltip!!.addPara("All skills except for \"System Expertise\" and \"Missile Specialization\" require just 1 skill point. The skills just mentioned require 2.", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "System Expertise", "Missile Specialization", "1", "2")

                tooltip.addSpacer(10f)

                var extra = ""
                if (storyPoins == 0) {
                    extra = "You do not have enough story points do so right now."
                }
                if (aptitudeIconElement.isInEditMode) {
                    extra = "Does not work while editing your active skills."
                }

                var label = tooltip!!.addPara("You can refund all combat skills by pressing \"R\" while hovering over this icon. This costs a story point to do. $extra", 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "")

                label.setHighlight("R", "story point", extra)
                label.setHighlightColors(Misc.getHighlightColor(), Misc.getStoryOptionColor(), Misc.getNegativeHighlightColor())

            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 400f
            }
        }, aptitudeIconElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )



        var count = 0
        var newLineAt = 7
        var anchor = subelement.addLunaElement(0f, 0f)
        //anchor.elementPanel.position.inTL(35f, 41f)
        anchor.elementPanel.position.inTL(128f, 45f)
        var previous: CustomPanelAPI = anchor.elementPanel
        var firstSkill: CustomPanelAPI? = null
        var usedWidth = 0f
        var skillElements = ArrayList<SkillWidgetElement>()
        for (skill in skills) {

            count += 1
            if (count == 8) {
                var lowerAnchor = subelement.addLunaElement(0f, 0f)
                lowerAnchor.elementPanel.position.belowLeft(anchor.elementPanel, 72f +7 + 8f)
                previous = lowerAnchor.elementPanel
            }


            var skillSpec = Global.getSettings().getSkillSpec(skill)

            var isFirst = skills.first() == skill
            var isLast = skills.last() == skill

            var preacquired = false
            var activated = false
            if (acquiredSkillsIds.contains(skill)) {
                preacquired = true
                activated = true
            }

            var skillElement = SkillWidgetElement(skill, "combat", activated, !preacquired, preacquired, skillSpec.spriteName, "combat2", color, subelement, 72f, 72f)
            skillElement.elementPanel.position.rightOfTop(previous, 16f)
            previous = skillElement.elementPanel
            skillElements.add(skillElement)

           /* var testTooltip = ReflectionUtils.invokeStatic(8, "createSkillTooltip", StandardTooltipV2::class.java,
                skillSpec, Global.getSector().playerPerson.stats,
                800f, 10f, true, false, 1000, null)

            ReflectionUtils.invokeStatic(2, "addTooltipBelow", StandardTooltipV2Expandable::class.java, skillElement.elementPanel, testTooltip)*/

            var skillTooltip = VanillaSkillTooltip.addToTooltip(subelement, player, skillSpec, getSkillPointCost(skill))

            skillElement.advance {
                if (skillElement.activated) {
                    skillTooltip.level = 2f
                } else {
                    skillTooltip.level = 0f
                }
            }

            if (count == 1) {
                firstSkill = skillElement.elementPanel
            }

            usedWidth+=72f

           /* if (count == 7) {
                var underline = SkillUnderlineElement(color, 1f, subelement, usedWidth)
                underline.position.belowLeft(firstSkill, 3f+8f)
            }*/



            /*if (!isLast && count != 7) {
                var seperator = SkillSeperatorElement(color, subelement)
                seperator.elementPanel.position.rightOfTop(skillElement.elementPanel, 6f)
                previous = seperator.elementPanel
                usedWidth+=7f
            }*/

        }

        for (skillElement in skillElements) {

            skillElement.onClick {
                if (!skillElement.preAcquired && skillElement.canChangeState) {
                    enterEditMode(subpanel, aptitudeIconElement, skillElements)

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

                var points = getCurrentSkillPointsUsed(skillElements)
                if (points == 0) {
                    exitEditMode(subpanel)
                }
            }

            skillElement.advance {
                var available = skillPoints
                var cost = getSkillPointCost(skillElement.id)
                if (!skillElement.preAcquired && !skillElement.activated && cost > available) {
                    skillElement.canChangeState = false
                } else if (!skillElement.preAcquired) {
                    skillElement.canChangeState = true
                }
            }

        }

        aptitudeIconElement.advance {
            skillPoints = startSkillPoints - getCurrentSkillPointsUsed(skillElements)
        }

        aptitudeIconElement.onInput {events ->
            if (aptitudeIconElement.isHovering) {
                for (event in events!!) {
                    if (event.isConsumed) continue
                    if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_R) {
                        event.consume()

                        if (!aptitudeIconElement.isInEditMode) {
                            resetSkills(subpanel, aptitudeIconElement, skillElements)
                        }

                        break
                    }
                }
            }
        }
    }

    fun resetSkills(subpanel: CustomPanelAPI, aptitudeIcon: CombatSkillWidgetElement, skillElements: ArrayList<SkillWidgetElement>) {
        var active = skillElements.filter { it.activated }
        var count = active.count()

        var storyPoints = Global.getSector().playerPerson.stats.storyPoints
        if (count == 0 || storyPoints == 0) {
            aptitudeIcon.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
            return
        }

        var combinedPoints = 0f
        for (skillElement in active) {
            combinedPoints += getSkillPointCost(skillElement.id)
            Global.getSector().playerPerson.stats.setSkillLevel(skillElement.id, 0f)
        }

        Global.getSector().playerPerson.stats.points += combinedPoints.toInt()
        Global.getSector().playerPerson.stats.storyPoints -= 1

        Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }

        aptitudeIcon.playSound(Sounds.STORY_POINT_SPEND)

        recreatePlayerPanel(subpanel)
    }

    fun enterEditMode(subpanel: CustomPanelAPI, aptitudeIcon: CombatSkillWidgetElement, skillElements: ArrayList<SkillWidgetElement>) {
        if (aptitudeIcon.isInEditMode) return
        aptitudeIcon.isInEditMode = true

        aptitudeIcon.innerElement.addSpacer(12f)

        var confirmButton = ConfirmCancelButton(aptitudeIcon.color, aptitudeIcon.innerElement, 82f, 30f).apply {
            addText("Confirm")
            centerText()

            onClick {
                playSound(Sounds.STORY_POINT_SPEND)
                saveSkillDataToCharacter(skillElements)
                exitEditMode(subpanel)

                if (Global.getSector().playerFleet?.fleetData != null) {
                    Global.getSector().playerFleet.fleetData.membersListCopy.forEach { it.updateStats() }
                }
            }
        }
        confirmButton.elementPanel.position.inTL(7f, 12f)

        var cancelButton = ConfirmCancelButton(aptitudeIcon.color, aptitudeIcon.innerElement, 82f, 30f).apply {
            addText("Cancel")
            centerText()

            onClick {
                playSound("ui_char_decrease_skill", 1f, 1f)
                exitEditMode(subpanel)
            }
        }

        cancelButton.elementPanel.position.belowLeft(confirmButton.elementPanel, 12f)
    }

    fun exitEditMode(subpanel: CustomPanelAPI) {
        recreatePlayerPanel(subpanel)
    }

    fun saveSkillDataToCharacter(skillElements: ArrayList<SkillWidgetElement>) {
        var activeSkills = skillElements.filter { it.activated }.map { it.id }

        var spCost = getCurrentSkillPointsUsed(skillElements)
        var stats = Global.getSector().playerPerson.stats

        for (active in activeSkills) {
            stats.setSkillLevel(active, 2f)
        }

        stats.points -= spCost
    }

    fun getSkillPointCost(skillId: String) : Int {
        if (skillId == "systems_expertise" || skillId == "missile_specialization") return 2
        return 1
    }

    fun getCurrentSkillPointsUsed(skillElements: List<SkillWidgetElement>) : Int {
        var points = 0
        for (element in skillElements) {
            if (!element.preAcquired && element.activated) {
                points += getSkillPointCost(element.id)
            }
        }
        return points
    }


    fun recreatePlayerPanel(subpanel: CustomPanelAPI) {

        skillPoints = Global.getSector().playerPerson.stats.points
        startSkillPoints = Global.getSector().playerPerson.stats.points

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
        }, nameElement.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT)


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

        subelement.addSpacer(7f)
        var level = Global.getSector().playerPerson.stats.level
        var levelText = "-Hover over the bar to view level up effects\n" +
                        "-Level $level"
        if (level >= Global.getSettings().levelupPlugin.maxLevel) levelText += " (maximum)"
        var levelPara = subelement.addPara("$levelText", 0f, Misc.getGrayColor(), Misc.getHighlightColor(),  "$level")

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




                tooltip.addSpacer(10f)
                tooltip.addSectionHeading("Level Up Effects", Alignment.MID, 0f)
                tooltip.addSpacer(10f)

                for (i in 0 .. maxLevel) {
                    var text = PlayerLevelEffects.getTooltipForLevel(i)
                    if (text == "") continue
                    var color = PlayerLevelEffects.getColor(i)

                    tooltip.addPara(" - Lv$i: $text",  0f, color, Misc.getHighlightColor(), "Lv$i:")
                }

                tooltip.addSpacer(5f)
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 410f
            }
        }, xpBar.elementPanel, TooltipMakerAPI.TooltipLocation.RIGHT )


        subelement.addSpacer(10f)
/*
        var color = Global.getSettings().getSkillSpec("aptitude_combat").governingAptitudeColor
        var confirmButton = subelement.addLunaElement(125f, 30f).apply {
            backgroundAlpha = 0.2f
            borderAlpha = 0.5f
            enableTransparency = true
            backgroundColor = color
            borderColor = color

            innerElement.setParaFont("graphics/fonts/victor14.fnt")
            addText("Confirm")
            centerText()
        }

        var cancelButton = subelement.addLunaElement(125f, 30f).apply {
            backgroundAlpha = 0.2f
            borderAlpha = 0.5f
            enableTransparency = true
            backgroundColor = color
            borderColor = color

            innerElement.setParaFont("graphics/fonts/victor14.fnt")
            addText("Cancel")
            centerText()
        }
        cancelButton.elementPanel.position.rightOfTop(confirmButton.elementPanel, 10f)*/

        var line = subelement.addLunaElement(2f, 240f).apply {
            enableTransparency = true
            renderBorder = false
        }

        line.elementPanel.position.rightOfTop(nameElement.elementPanel, 20f)


        recreateAptitudePanel(subpanel)
    }



}