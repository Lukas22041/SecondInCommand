package second_in_command.skills.smallcraft

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class LeaderOfThePack : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers close to cruisers or capitals with assigned officers receive increased stats", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The radius for this effect is approximately 1000 su wide",0f, Misc.getTextColor(), Misc.getHighlightColor(), "1000")
        tooltip.addPara("   - Ships under the effect gain 15%% damage resistance towards all sources",0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - Ships under the effect gain a 10%% increase towards all weapon ranges",0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (!ship!!.hasListenerOfClass(LeaderOfThePackScript::class.java) && (ship.hullSize == ShipAPI.HullSize.FRIGATE || ship.hullSize == ShipAPI.HullSize.DESTROYER)) {
            ship.addListener(LeaderOfThePackScript(ship))
        }
    }

}

class LeaderOfThePackScript(var ship: ShipAPI) : AdvanceableListener {

    var interval = IntervalUtil(0.2f, 0.2f)

    var isActive = false

    override fun advance(amount: Float) {

        if (ship == Global.getCombatEngine().playerShip && isActive) {

            Global.getCombatEngine().maintainStatusForPlayerShip("sc_leader_of_the_pack", "graphics/icons/hullsys/targeting_feed.png",
                "Leader of the Pack", "15% DR / 10% Weapon Range", false)
        }

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        isActive = false

        var iterator = Global.getCombatEngine().shipGrid.getCheckIterator(ship.location, 2000f, 2000f)
        for (entry in iterator) {
            var ally = entry as ShipAPI

            if (ally == ship) continue
            if (!ally.isAlive) continue
            if (ship.owner != ally.owner) continue

            if (ally.captain == null) continue
            if (ally.captain.isDefault) continue

            if (!ally.isCruiser && !ally.isCapital) continue
            if (MathUtils.getDistance(ally, ship) >= 1000) continue

            isActive = true
        }

        if (isActive) {
            ship.mutableStats.hullDamageTakenMult.modifyMult("sc_leader_of_the_pack", 0.85f)
            ship.mutableStats.armorDamageTakenMult.modifyMult("sc_leader_of_the_pack", 0.85f)
            ship.mutableStats.shieldDamageTakenMult.modifyMult("sc_leader_of_the_pack", 0.85f)

            ship.mutableStats.ballisticWeaponRangeBonus.modifyPercent("sc_leader_of_the_pack", 10f)
            ship.mutableStats.energyWeaponRangeBonus.modifyPercent("sc_leader_of_the_pack", 10f)
            ship.mutableStats.missileWeaponRangeBonus.modifyPercent("sc_leader_of_the_pack", 10f)

        } else {
            ship.mutableStats.hullDamageTakenMult.unmodify("sc_leader_of_the_pack")
            ship.mutableStats.armorDamageTakenMult.unmodify("sc_leader_of_the_pack")
            ship.mutableStats.shieldDamageTakenMult.unmodify("sc_leader_of_the_pack")

            ship.mutableStats.ballisticWeaponRangeBonus.unmodify("sc_leader_of_the_pack")
            ship.mutableStats.energyWeaponRangeBonus.unmodify("sc_leader_of_the_pack")
            ship.mutableStats.missileWeaponRangeBonus.unmodify("sc_leader_of_the_pack")
        }
    }
}