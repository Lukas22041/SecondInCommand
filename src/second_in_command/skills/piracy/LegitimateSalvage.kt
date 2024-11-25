package second_in_command.skills.piracy

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class LegitimateSalvage : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "salvage procedures"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+20%% post-battle salvage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+15%% to the chance that opponents drop their weapons after being destroyed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% resources - but not rare items, such as blueprints - recovered from abandoned stations and other derelicts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {


    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }


    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_innovative_salvage", 0.3f)
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_innovative_salvage", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_innovative_salvage", 0.15f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).modifyFlat("sc_innovative_salvage", 0.15f)
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_innovative_salvage", 0.3f)
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_innovative_salvage", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_innovative_salvage", 0.15f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).modifyFlat("sc_innovative_salvage", 0.15f)
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).unmodify("sc_innovative_salvage")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).unmodify("sc_innovative_salvage")
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).unmodify("sc_innovative_salvage")
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).unmodify("sc_innovative_salvage")
    }
}