package second_in_command.skills.tactical

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class PristineCondition : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("+5%% combat readiness for every s-mod on the ship (max 15%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        
    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var smods = variant.sMods.count()
        var bonus = 0.05f * smods
        bonus = bonus.coerceIn(0f, 0.15f)

        stats!!.maxCombatReadiness.modifyFlat(id, bonus, "Pristine Condition")
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}