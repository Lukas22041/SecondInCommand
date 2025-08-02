package second_in_command.skills.special.christmas

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.Salvaging
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ChristmasSpirit : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet and salvage procedures"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+30%% resources - including rare items, such as blueprints - recovered from abandoned stations and derelicts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+70%% resources - but not rare items, such as blueprints - recovered from abandoned stations and derelicts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+50%% post-battle salvage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+20%% to the chance that opponents drop their weapons after being destroyed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% cargo and fuel capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+2 to burn level at which the fleet is considered to be moving slowly*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("This officer will depart from the fleet after the 27th december.", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "depart", "27th")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.cargoMod.modifyPercent(id, 30f)
        stats!!.fuelMod.modifyPercent(id, 30f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).modifyFlat("sc_christmas_spirit", 0.3f, "Christmas Spirit")
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_christmas_spirit", 0.7f, "Christmas Spirit")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_christmas_spirit", 0.5f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_christmas_spirit", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).modifyFlat("sc_christmas_spirit", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).modifyFlat("sc_christmas_spirit", 2f, "Christmas Spirit")

    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).modifyFlat("sc_christmas_spirit", 0.3f, "Christmas Spirit")
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat("sc_christmas_spirit", 0.7f, "Christmas Spirit")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).modifyFlat("sc_christmas_spirit", 0.5f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_christmas_spirit", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).modifyFlat("sc_christmas_spirit", 0.2f)
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).modifyFlat("sc_christmas_spirit", 2f, "Christmas Spirit")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).unmodify("sc_christmas_spirit")
        data.fleet.stats.dynamic.getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).unmodify("sc_christmas_spirit")
        data.fleet.stats.dynamic.getStat(Stats.BATTLE_SALVAGE_MULT_FLEET).unmodify("sc_christmas_spirit")
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).unmodify("sc_christmas_spirit")
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WING_RECOVERY_MOD).unmodify("sc_christmas_spirit")
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).unmodify("sc_christmas_spirit")
    }
}