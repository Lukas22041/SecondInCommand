package second_in_command.skills.management

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class WellOrganized : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("You are more likely to find officers on markets", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-33%% required minimum crew for all ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-33%% crew lost during deployment", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.minCrewMod.modifyMult(id, 0.666f)
        stats.crewLossMult.modifyMult(id, 0.666f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun onActivation(data: SCData?) {
        if (data!!.isPlayer && !Global.getSector().hasScript(WellOrganisedScript::class.java)) {
            Global.getSector().addScript(WellOrganisedScript())
        }
    }

    override fun onDeactivation(data: SCData?) {
        if (data!!.isPlayer) {
            var script = Global.getSector().scripts.find { it is WellOrganisedScript }
            if (script != null) {
                Global.getSector().removeScript(script)

                for (market in Global.getSector().economy.marketsCopy) {
                    market.stats.dynamic.getMod(Stats.OFFICER_PROB_MOD).unmodify("sic_well_organised")
                    market.stats.dynamic.getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).unmodify("sic_well_organised")
                }
            }
        }
    }

    class WellOrganisedScript() : EveryFrameScript {

        var interval = IntervalUtil(2f, 3f)

        override fun isDone(): Boolean {
            return false
        }


        override fun runWhilePaused(): Boolean {
            return true
        }

        override fun advance(amount: Float) {
            interval.advance(amount)

            if (interval.intervalElapsed()) {
                for (market in Global.getSector().economy.marketsCopy) {
                    market.stats.dynamic.getMod(Stats.OFFICER_PROB_MOD).modifyFlat("sic_well_organised", 0.1f)
                    market.stats.dynamic.getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyFlat("sic_well_organised", 0.1f)

                    market.stats.dynamic.getMod(Stats.OFFICER_PROB_MOD).modifyMult("sic_well_organised", 1.2f)
                    market.stats.dynamic.getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyMult("sic_well_organised", 1.2f)
                }
            }


        }

    }

}