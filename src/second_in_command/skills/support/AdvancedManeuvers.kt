package second_in_command.skills.support

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

        tooltip.addPara("+25%% top speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% damage dealt", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+15%% weapon fire rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.maxSpeed.modifyPercent(id, 25f)
        stats.acceleration.modifyPercent(id, 20f)
        stats.deceleration.modifyPercent(id, 20f)

        stats.ballisticWeaponDamageMult.modifyMult(id, 1.1f)
        stats.energyWeaponDamageMult.modifyMult(id, 1.1f)
        stats.missileWeaponDamageMult.modifyMult(id, 1.1f)

        stats.ballisticRoFMult.modifyMult(id, 1.15f)
        stats.energyRoFMult.modifyMult(id, 1.15f)
        stats.missileRoFMult.modifyMult(id, 1.15f)

    }

}