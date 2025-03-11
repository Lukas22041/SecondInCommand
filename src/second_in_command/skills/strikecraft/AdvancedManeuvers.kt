package second_in_command.skills.strikecraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class AdvancedManeuvers : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighters"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+10%% damage dealt", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+15%% weapon fire rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+50%% target leading accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.autofireAimAccuracy.modifyFlat(id, 0.5f)

        /* stats.maxSpeed.modifyPercent(id, 25f)
         stats.acceleration.modifyPercent(id, 25f*2)
         stats.deceleration.modifyPercent(id, 25f*2)*/

        stats.ballisticWeaponDamageMult.modifyPercent(id, 10f)
        stats.energyWeaponDamageMult.modifyPercent(id, 10f)
        stats.missileWeaponDamageMult.modifyPercent(id, 10f)

        stats.ballisticRoFMult.modifyPercent(id, 15f)
        stats.energyRoFMult.modifyPercent(id, 15f)
        stats.missileRoFMult.modifyPercent(id, 15f)

    }

}