package second_in_command.skills.piracy

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Steadfast : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+5%% maximum combat readiness", 0f, Misc.getHighlightColor(), Misc.getHighlightColor(), "Steadfast")
        tooltip.addPara("+20%% weapon and engine repair speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-30%% weapon and engine damage taken", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.weaponDamageTakenMult.modifyMult(id, 0.7f)
        stats.engineDamageTakenMult.modifyMult(id, 0.7f)

        stats.combatEngineRepairTimeMult.modifyMult(id, 0.8f)
        stats.combatWeaponRepairTimeMult.modifyMult(id, 0.8f)

        stats.maxCombatReadiness.modifyFlat(id, 0.05f, "Steadfast")
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}