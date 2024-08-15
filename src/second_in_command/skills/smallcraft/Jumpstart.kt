package second_in_command.skills.smallcraft

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Jumpstart : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers receive increased stats for the first minute after combat began", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 50%% faster objective capture rate",0f, Misc.getTextColor(), Misc.getHighlightColor(), "50%")
        tooltip.addPara("   - 20%% increased max speed",0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
       // tooltip.addPara("   - 10%% more damage dealt",0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var test = ""

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

        var test = ""

    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {
        if (!ship!!.isFrigate && !ship.isDestroyer) return

        var time = Global.getCombatEngine().getTotalElapsedTime(false)

        if (time <= 60) {

            if (ship == Global.getCombatEngine().playerShip) {
                Global.getCombatEngine().maintainStatusForPlayerShip("sc_jumpstart", "graphics/icons/hullsys/damper_field.png",
                    "Jumpstart", "${(60-time).toInt()} seconds remaining", false)
            }

            ship!!.mutableStats.maxSpeed.modifyPercent("sc_jumpstart", 20f)

           /* ship.mutableStats.ballisticWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)
            ship.mutableStats.energyWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)
            ship.mutableStats.missileWeaponDamageMult.modifyMult("sc_jumpstart", 1.1f)*/

            ship.mutableStats.dynamic.getStat(Stats.SHIP_OBJECTIVE_CAP_RATE_MULT).modifyMult("sc_jumpstart", 1.5f)
        }
        else {
            ship!!.mutableStats.maxSpeed.unmodify("sc_jumpstart")

            /*ship.mutableStats.ballisticWeaponDamageMult.unmodify("sc_jumpstart")
            ship.mutableStats.energyWeaponDamageMult.unmodify("sc_jumpstart")
            ship.mutableStats.missileWeaponDamageMult.unmodify("sc_jumpstart")*/

            ship.mutableStats.dynamic.getStat(Stats.SHIP_OBJECTIVE_CAP_RATE_MULT).unmodify("sc_jumpstart")
        }
    }

}