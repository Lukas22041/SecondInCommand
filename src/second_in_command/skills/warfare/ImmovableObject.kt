package second_in_command.skills.warfare

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ImmovableObject : SCBaseSkillPlugin() {


    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+20%% repair speed for engines and weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% hitpoints for engines and weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+100%% ship mass", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Ship mass is primarily used for collision damage calculations", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "collision damage")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.weaponHealthBonus.modifyPercent(id, 30f)
        stats!!.engineHealthBonus.modifyPercent(id, 30f)

        stats!!.combatEngineRepairTimeMult.modifyMult(id, 0.8f)
        stats!!.combatWeaponRepairTimeMult.modifyMult(id, 0.8f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

        if (ship.customData.containsKey("sc_immoveable_object")) return
        ship.mass += ship.mass
        ship.setCustomData("sc_immoveable_object", true)

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}