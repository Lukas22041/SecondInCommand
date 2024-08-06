package second_in_command.skills.piracy

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class HuntingGrounds : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+15%% top speed for frigates and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% maneuverability for frigates and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (hullSize == ShipAPI.HullSize.FRIGATE || hullSize == ShipAPI.HullSize.DESTROYER) {
            stats!!.maxSpeed.modifyPercent(id, 15f)
            stats!!.acceleration.modifyPercent(id, 10f)
            stats!!.deceleration.modifyPercent(id, 10f)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}