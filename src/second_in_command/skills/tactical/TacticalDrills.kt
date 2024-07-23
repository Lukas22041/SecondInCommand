package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class TacticalDrills : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("All ships have slightly improved autofire accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+5%% weapon damage for all ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.ballisticWeaponDamageMult.modifyMult(id, 1.05f)
        stats.energyWeaponDamageMult.modifyMult(id, 1.05f)
        stats.missileWeaponDamageMult.modifyMult(id, 1.05f)

        stats.autofireAimAccuracy.modifyFlat(id, 0.2f)
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}