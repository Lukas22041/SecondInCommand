package second_in_command.skills.scavenging

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Explorer : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+1 maximum burn level", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+20%% cargo capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+20%% fuel capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        //tooltip.addPara("+20%% crew capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% sensor range", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {
        stats!!.cargoMod.modifyPercent(id, 20f)
        stats!!.fuelMod.modifyPercent(id, 20f)
        //stats!!.maxCrewMod.modifyPercent(id, 20f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_explorer", 1f, "Explorer Skill")
        data.fleet.stats.sensorRangeMod.modifyPercent("sc_explorer", 10f, "Explorer Skill")
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_explorer", 1f, "Explorer Skill")
        data.fleet.stats.sensorRangeMod.modifyPercent("sc_explorer", 10f, "Explorer Skill")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.fleetwideMaxBurnMod.unmodify("sc_explorer")
        data.fleet.stats.sensorRangeMod.unmodify("sc_explorer")
    }

}