package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class TacticalDrills : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All ships have slightly improved autofire accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+5%% weapon damage for all ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.ballisticWeaponDamageMult.modifyPercent(id, 5f)
        stats.energyWeaponDamageMult.modifyPercent(id, 5f)
        stats.missileWeaponDamageMult.modifyPercent(id, 5f)

        stats.autofireAimAccuracy.modifyFlat(id, 0.2f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}