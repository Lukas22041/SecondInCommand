package second_in_command.skills.starfaring

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class BulkTransport : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+40%% cargo capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+40%% fuel capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+40%% personnel capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.cargoMod.modifyPercent(id, 40f)
        stats!!.fuelMod.modifyPercent(id, 40f)
        stats!!.maxCrewMod.modifyPercent(id, 40f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}