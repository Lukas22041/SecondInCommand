package second_in_command.skills.starfaring

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.Navigation
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.logger
import second_in_command.specs.SCBaseSkillPlugin

class Navigation : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+1 maximum burn level", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+1 maximum burn level for the \"Sustained Burn\" ability", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-30%% terrain movement penalty from all applicable terrain", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.commander.stats.dynamic.getStat(Stats.NAVIGATION_PENALTY_MULT).modifyFlat("sc_navigation", -0.3f)
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_navigation", 1f, "Navigation Skill")
        data.fleet.stats.dynamic.getMod(Stats.SUSTAINED_BURN_BONUS).modifyFlat("sc_navigation", 1f, "Navigation Skill")
    }

    override fun onActivation(data: SCData) {
        data.commander.stats.dynamic.getStat(Stats.NAVIGATION_PENALTY_MULT).modifyFlat("sc_navigation", -0.3f)
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_navigation", 1f, "Navigation Skill")
        data.fleet.stats.dynamic.getMod(Stats.SUSTAINED_BURN_BONUS).modifyFlat("sc_navigation", 1f, "Navigation Skill")
    }

    override fun onDeactivation(data: SCData) {
        data.commander.stats.dynamic.getStat(Stats.NAVIGATION_PENALTY_MULT).unmodify("sc_navigation")
        data.fleet.stats.fleetwideMaxBurnMod.unmodify("sc_navigation")
        data.fleet.stats.dynamic.getMod(Stats.SUSTAINED_BURN_BONUS).unmodify("sc_navigation")
    }

}