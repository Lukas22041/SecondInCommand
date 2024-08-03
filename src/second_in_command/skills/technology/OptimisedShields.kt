package second_in_command.skills.technology

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class OptimisedShields : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships with shields"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The flux required to keep shields active is cut in half", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())


    }

    override fun applyEffectsBeforeShipCreation(data: SCData,
                                                stats: MutableShipStatsAPI?,
                                                variant: ShipVariantAPI,
                                                hullSize: ShipAPI.HullSize?,
                                                id: String?) {

        stats!!.shieldUpkeepMult.modifyMult(id, 0.5f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }



}