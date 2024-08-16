package second_in_command.skills.improvisation

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ScrappyMaintenance : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All ships are more likely to receive d-mods after being disabled in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+3%% combat readiness per d-mod (maximum effect at 5)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+5%% repair rate outside of combat per d-mod (maximum effect at 5)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var dmods = 0

        var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

        var hmods = variant.permaMods
        for (hmod in hmods) {
            if (dmodSpecs.map { it.id }.contains(hmod)) {
                dmods += 1
            }
        }

        dmods = dmods.coerceIn(0, 5)

        var cr = 0.03f * dmods
        var repairRate = 5f * dmods

        stats.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 1.5f)
        stats.maxCombatReadiness.modifyFlat(id, cr, "Scrappy maintenance")
        stats.baseCRRecoveryRatePercentPerDay.modifyPercent(id, repairRate)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amunt: Float?) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }


}