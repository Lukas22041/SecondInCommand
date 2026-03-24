package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class MassBombardment : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+30%% missile weapon rate of fire", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+40%% ammo capacity of missile weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+40%% ammo regeneration of rechargeable missile weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-15%% missile weapon damage", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())

        
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.missileRoFMult.modifyPercent(id, 30f)
        stats!!.missileAmmoBonus.modifyPercent(id, 40f)
        stats!!.missileAmmoRegenMult.modifyPercent(id, 40f)
        stats!!.missileWeaponDamageMult.modifyMult(id, 0.85f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}