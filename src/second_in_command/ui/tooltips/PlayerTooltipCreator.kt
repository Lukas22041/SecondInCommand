package second_in_command.ui.tooltips

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.OfficerXPBar

class PlayerTooltipCreator(var player: PersonAPI) : BaseTooltipCreator() {




    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 350f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
        var plugin = SCSpecStore.getAptitudeSpec("sc_fake_combat_aptitude")!!.getPlugin()
        var width = getTooltipWidth(null)

        var title = tooltip!!.addTitle(player.nameString, plugin.getColor())
        var xPos = width / 2 - title.computeTextWidth(title.text) / 2
        title.position.inTL(xPos, 5f)

        var levelPlugin = Global.getSettings().levelupPlugin

        var bar = OfficerXPBar(player.stats.xp.toFloat(), levelPlugin.getXPForLevel(player.stats.level+1).toFloat(), plugin.getColor(), tooltip!!, 180f, 25f).apply {
            position.inTMid(25f)
        }


        bar.addText("Lv ${player.stats.level}", Misc.getTextColor())
        bar.centerText()

        var experience = player.stats.xp
        var experienceNeeded = levelPlugin.getXPForLevel(player.stats.level+1) - experience
        var inactiveGain = (SCOfficer.inactiveXPMult * 100).toInt()

        var firstPara = tooltip.addPara("${player.nameString} has experience within the ${plugin.getName()} aptitude. This aptitude has a maximum level of ${levelPlugin.maxLevel}.", 0f,
        Misc.getTextColor(), Misc.getHighlightColor(), "")
        firstPara.position.inTL(7.5f, 60f)

        tooltip.addSpacer(10f)

        firstPara.setHighlight(player.nameString, plugin.getName(), "${levelPlugin.maxLevel}")
        firstPara.setHighlightColors(Misc.getHighlightColor(), plugin.getColor(), Misc.getHighlightColor())

        tooltip.addPara("${player.heOrShe.capitalize()} is currently at level ${player.stats.level}. " +
                "${player.heOrShe.capitalize()} has $experience experience points and requires $experienceNeeded more to level up.", 0f,
        Misc.getTextColor(), Misc.getHighlightColor(), "${player.stats.level}", "$experience", "$experienceNeeded")

        tooltip.addSpacer(10f)

        tooltip.addPara("All officers gain experience from battles. Inactive officers also earn experience, but at $inactiveGain%% of the normal rate.", 0f, Misc.getTextColor(), Misc.getHighlightColor(),
            "gain experience from battles" ,"$inactiveGain%")

        tooltip.addSpacer(30f)

    }



}