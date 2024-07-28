package second_in_command.skills.support

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.WolfpackTactics
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class SystemProficiency : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighters with shipsystems"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("If the shipsystem has charges: +1 charge", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("If the shipsystem regenerates charges: +40%% regeneration rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("If the shipsystem has a cooldown: -33%% cooldown", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.systemUsesBonus.modifyFlat(id, 1f)
        stats.systemRegenBonus.modifyPercent(id, 40f)
        stats.systemCooldownBonus.modifyMult(id, 0.666f)
    }

}