package second_in_command.skills.management

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.SupportDoctrine
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.levelBetween
import second_in_command.specs.SCBaseSkillPlugin

class InGoodHands : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships with officers"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships with officers have a reduction in deployment points cost", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The cost reduction is between 0%%-15%% of the ships total, based on officer level", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "0%", "15%")
        tooltip.addPara("   - The maximum effect is reached at level 7", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "7")
        tooltip.addPara("   - The reduction can not be more than 10 points", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10")

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var captain = stats!!.fleetMember?.captain ?: return

        if (captain.isDefault || captain.isAICore) return

        var level = captain.stats.level.toFloat()
        var scale = level.levelBetween(0f, 7f)
        var reductionPercent = 0.15f * scale

        val baseCost = stats.suppliesToRecover.baseValue
        val reduction = Math.min(10f, baseCost * reductionPercent)

        stats.dynamic.getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, (-reduction).toFloat())

    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }


}