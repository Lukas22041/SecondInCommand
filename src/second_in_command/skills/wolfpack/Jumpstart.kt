package second_in_command.skills.wolfpack

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class Jumpstart : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers receive increased stats during the first minute of deployment", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 40%% faster objective capture rate",0f, Misc.getTextColor(), Misc.getHighlightColor(), "40%")
        tooltip.addPara("   - 20%% increased max speed",0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - 10%% more damage dealt",0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advanceInCombat(ship: ShipAPI?, amount: Float) {
        var time = Global.getCombatEngine().getTotalElapsedTime(false)

        if (time <= 60) {
            ship!!.mutableStats.maxSpeed.modifyMult("sc_jumpstart", 1.2f)

            ship.mutableStats.ballisticWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)
            ship.mutableStats.energyWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)
            ship.mutableStats.missileWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)

            ship.mutableStats.dynamic.getStat(Stats.SHIP_OBJECTIVE_CAP_RATE_MULT).modifyMult("sc_jumpstart", 1.4f)
        }
        else {
            ship!!.mutableStats.maxSpeed.unmodify("sc_jumpstart")

            ship.mutableStats.ballisticWeaponDamageMult.unmodify("sc_jumpstart")
            ship.mutableStats.energyWeaponDamageMult.unmodify("sc_jumpstart")
            ship.mutableStats.missileWeaponDamageMult.unmodify("sc_jumpstart")

            ship.mutableStats.dynamic.getStat(Stats.SHIP_OBJECTIVE_CAP_RATE_MULT).unmodify("sc_jumpstart")
        }
    }

}