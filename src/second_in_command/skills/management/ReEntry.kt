package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ReEntry : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Ships deployed a minute after combat began gain a large increase in speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ships maximum speed is increased by 75", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "75")
        tooltip.addPara("   - The increase in speed is nullified the moment its weapons are in range of a hostile ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "75")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        var time = Global.getCombatEngine()?.getTotalElapsedTime(false) ?: return

        if (time >= 60 && Global.getCombatEngine()?.ships?.contains(ship) == false) {
            ship!!.mutableStats.maxSpeed.modifyFlat("sc_re_entry", 75f)
            ship!!.mutableStats.acceleration.modifyFlat("sc_re_entry", 40f)
            ship!!.mutableStats.deceleration.modifyFlat("sc_re_entry", 40f)
        }
    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

        if (ship!!.areAnyEnemiesInRange()) {
            ship.mutableStats.maxSpeed.unmodify("sc_re_entry")
            ship.mutableStats.acceleration.unmodify("sc_re_entry")
            ship.mutableStats.deceleration.unmodify("sc_re_entry")
        }

    }
}