package second_in_command.skills.wolfpack

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class QuickAsTheWind : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers have much faster recovery of their shipsystem", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 33%% reduced shipsystem cooldown",0f, Misc.getTextColor(), Misc.getHighlightColor(), "33%")
        tooltip.addPara("   - 33%% increased shipsystem charge regeneration",0f, Misc.getTextColor(), Misc.getHighlightColor(), "33%")

        tooltip.addSpacer(10f)

        tooltip.addPara("+20%% to non-missile ammunition recharge and capacity for all frigates and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (hullSize != ShipAPI.HullSize.FRIGATE && hullSize != ShipAPI.HullSize.DESTROYER) return

        stats!!.systemRegenBonus.modifyPercent(id, 33f)
        stats.systemCooldownBonus.modifyMult(id, 0.666f)



        stats.ballisticAmmoBonus.modifyPercent(id, 20f)
        stats.energyAmmoBonus.modifyPercent(id, 20f)

        stats.ballisticAmmoRegenMult.modifyPercent(id, 25f)
        stats.energyAmmoRegenMult.modifyPercent(id, 25f)
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}