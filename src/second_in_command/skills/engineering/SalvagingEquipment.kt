package second_in_command.skills.engineering

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SalvagingEquipment : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+40%% resources - but not rare items, such as blueprints - recovered from abandoned stations and derelicts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% post battle salvage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-75%% crew lost in non-combat operations", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getStat(Stats.NON_COMBAT_CREW_LOSS_MULT).modifyMult("sc_salvaging_equipment", 0.25f)
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_salvaging_equipment", 0.4f, "Salvaging Equipment Skill")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_salvaging_equipment", 0.3f, "Salvaging Equipment Skill")
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.NON_COMBAT_CREW_LOSS_MULT).modifyMult("sc_salvaging_equipment", 0.25f)
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_salvaging_equipment", 0.4f, "Salvaging Equipment Skill")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_salvaging_equipment", 0.3f, "Salvaging Equipment Skill")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.NON_COMBAT_CREW_LOSS_MULT).unmodify("sc_salvaging_equipment")
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).unmodify("sc_salvaging_equipment")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).unmodify("sc_salvaging_equipment")
    }

}