package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseSkillPlugin

class Countermeasures : SCBaseSkillPlugin() {

    var PER_SHIP_BONUS = 1f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("Every deployed ship contributes ${PER_SHIP_BONUS.toInt()}%% to the ECM rating* of the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("All ships have slightly improved autofire accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.dynamic.getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, PER_SHIP_BONUS)
        stats.autofireAimAccuracy.modifyFlat(id, 0.2f)

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }



}