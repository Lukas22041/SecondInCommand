package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Authority : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+5%% to all ships speed and maneuverability for every combat objective held", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+5%% to all weapons range for every combat objective held", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

        var objectives = Global.getCombatEngine().objectives
        var held = objectives.filter { it.owner == ship!!.owner }.count()

        var bonus = 1f + (0.05f * held)

        var stats = ship!!.mutableStats

        stats.maxSpeed.modifyMult("sc_authority", bonus)
        stats.acceleration.modifyMult("sc_authority", bonus)
        stats.deceleration.modifyMult("sc_authority", bonus)

        stats.energyWeaponRangeBonus.modifyMult("sc_authority", bonus)
        stats.ballisticWeaponRangeBonus.modifyMult("sc_authority", bonus)
        stats.missileWeaponRangeBonus.modifyMult("sc_authority", bonus)

    }

}