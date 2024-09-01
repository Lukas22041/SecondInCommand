package second_in_command.skills.starfaring

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.MakeshiftEquipment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class MakeshiftEquipment : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-25%% monthly supply consumption for ship maintenance", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-50%% resources required to survey planets", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.suppliesPerMonth.modifyMult(id, 0.75f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getStat(Stats.SURVEY_COST_MULT).modifyMult("sc_makeshift_equipment", 0.5f, "Makeshift Equipment Skill");
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SURVEY_COST_MULT).modifyMult("sc_makeshift_equipment", 0.5f, "Makeshift Equipment Skill");
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SURVEY_COST_MULT).unmodify("sc_makeshift_equipment");
    }


}