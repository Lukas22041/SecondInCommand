package second_in_command.skills.strikecraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FighterUplink : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighters"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-50%% crew loss due to fighter losses in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+50%% target leading accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% top speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.maxSpeed.modifyPercent(id, 10f)
        stats.acceleration.modifyPercent(id, 10f * 2f)
        stats.deceleration.modifyPercent(id, 10f * 2f)

        stats.autofireAimAccuracy.modifyFlat(id, 0.5f)

        stats.dynamic.getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyMult(id, 0.5f)
    }

}