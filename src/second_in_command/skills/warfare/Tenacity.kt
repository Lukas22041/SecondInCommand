package second_in_command.skills.warfare

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Tenacity : SCBaseSkillPlugin() {


    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+75 armor for damage reduction calculations only", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        /*when (hullSize) {
            ShipAPI.HullSize.FRIGATE -> stats!!.effectiveArmorBonus.modifyFlat(id, 75f)
            ShipAPI.HullSize.DESTROYER -> stats!!.effectiveArmorBonus.modifyFlat(id, 100f)
            ShipAPI.HullSize.CRUISER -> stats!!.effectiveArmorBonus.modifyFlat(id, 125f)
            ShipAPI.HullSize.CAPITAL_SHIP -> stats!!.effectiveArmorBonus.modifyFlat(id, 150f)
            else -> null
        }*/
        stats!!.effectiveArmorBonus.modifyFlat(id, 75f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}