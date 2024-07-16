package second_in_command.skills.wolfpack

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.specs.SCBaseSkillPlugin

class TogetherAsOne : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers receive increased stats when near to other allied frigates and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The radius for this effect is approximately 1200 su wide",0f, Misc.getTextColor(), Misc.getHighlightColor(), "1200")
        tooltip.addPara("   - This effect maxes out at 6 nearby ships",0f, Misc.getTextColor(), Misc.getHighlightColor(), "6")
        tooltip.addPara("   - Per ship they gain a 3%% increase in maximum speed, flux dissipation and all damage dealt",0f, Misc.getTextColor(), Misc.getHighlightColor(), "3%", "maximum speed", "flux dissipation", "all damage dealt")

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (!ship!!.hasListenerOfClass(TogetherAsOneScript::class.java) && (ship.hullSize == ShipAPI.HullSize.FRIGATE || ship.hullSize == ShipAPI.HullSize.DESTROYER)) {
            ship.addListener(TogetherAsOneScript(ship))
        }
    }

}

class TogetherAsOneScript(var ship: ShipAPI) : AdvanceableListener {

    var interval = IntervalUtil(0.2f, 0.2f)
    var recentCount = 0

    override fun advance(amount: Float) {

        if (ship == Global.getCombatEngine().playerShip && recentCount > 0) {

            var addedS = ""
            if (recentCount >= 2) {
                addedS = "s"
            }

            Global.getCombatEngine().maintainStatusForPlayerShip("sc_together_as_one", "graphics/icons/hullsys/targeting_feed.png",
                "Together as One", "$recentCount nearby ship$addedS", false)
        }

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        var count = 0

        var iterator = Global.getCombatEngine().shipGrid.getCheckIterator(ship.location, 2000f, 2000f)
        for (entry in iterator) {
            var ally = entry as ShipAPI

            if (ally == ship) continue
            if (!ally.isAlive) continue
            if (ship.owner != ally.owner) continue

            if (!ally.isFrigate && !ally.isDestroyer) continue
            if (MathUtils.getDistance(ally, ship) >= 1200) continue

            count += 1
        }


        count = MathUtils.clamp(count, 0, 6)
        var mod = 0.03f * count

        ship.mutableStats.maxSpeed.modifyMult("sc_together_as_one", 1f + mod)
        ship.mutableStats.acceleration.modifyMult("sc_together_as_one", 1f + mod)
        ship.mutableStats.deceleration.modifyMult("sc_together_as_one", 1f + mod)

        ship.mutableStats.fluxDissipation.modifyMult("sc_together_as_one", 1f + mod)

        ship.mutableStats.ballisticWeaponDamageMult.modifyMult("sc_together_as_one", 1f + mod)
        ship.mutableStats.energyWeaponDamageMult.modifyMult("sc_together_as_one", 1f + mod)
        ship.mutableStats.missileWeaponDamageMult.modifyMult("sc_together_as_one", 1f + mod)


        recentCount = count

    }
}