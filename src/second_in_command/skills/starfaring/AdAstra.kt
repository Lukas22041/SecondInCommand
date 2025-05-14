package second_in_command.skills.starfaring

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class AdAstra : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+2 burn speed in hyperspace", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-25%% fuel usage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-25%% damage from hazards such as hyperspace storms and solar flares", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.fuelUseMod.modifyMult(id, 0.75f)
        stats.dynamic.getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 0.75f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        if (data.fleet.isInHyperspace) {
            data.fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_ad_astra",2f, "Ad Astra")
        }
        else {
            data.fleet.stats.fleetwideMaxBurnMod.unmodifyFlat("sc_ad_astra")
        }
    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.fleetwideMaxBurnMod.unmodifyFlat("sc_ad_astra")
    }

}