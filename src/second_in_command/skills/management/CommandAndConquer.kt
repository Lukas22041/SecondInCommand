package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class CommandAndConquer : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("+3 command points in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("50%% faster command point recovery in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        var player = Global.getSector().characterData.person
        player.stats.commandPoints.modifyFlat("sc_command_and_conquer", 3f)
        player.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).modifyFlat("sc_command_and_conquer", 0.5f)


    }

    override fun advance(amount: Float) {

        var player = Global.getSector().characterData.person
        player.stats.commandPoints.modifyFlat("sc_command_and_conquer", 3f)
        player.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).modifyFlat("sc_command_and_conquer", 0.5f)


    }

    override fun onDeactivation() {

        var player = Global.getSector().characterData.person
        player.stats.commandPoints.unmodify("sc_command_and_conquer")
        player.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_FLAT).unmodify("sc_command_and_conquer")
    }

}