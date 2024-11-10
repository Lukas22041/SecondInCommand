package second_in_command.skills.piracy

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ProvisionalReplacements : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        //tooltip.addPara("All ships are much more likely to be recoverable if lost in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% of hull and armor damage taken repaired after combat ends, at no cost", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("The supply recovery cost reduction from d-mods also applies to the ships monthly supply cost", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - In essence, this applies a 20%% reduction in monthly supply cost per d-mod", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        /*var dmods = 0

        var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

        var hmods = variant.permaMods
        for (hmod in hmods) {
            if (dmodSpecs.map { it.id }.contains(hmod)) {
                dmods += 1
            }
        }

        var bonus = 0.06f * dmods
        bonus = bonus.coerceIn(0f, 0.30f)

        stats!!.suppliesPerMonth.modifyMult(id, 1-bonus, "Provisional Replacements")*/

        var dmods = DModManager.getNumDMods(variant)

        var mult = 1f
        for (dmod in 0 until dmods) {
            mult *= 0.8f
        }
        stats!!.suppliesPerMonth.modifyMult(id, mult)

        stats!!.dynamic.getMod(Stats.INSTA_REPAIR_FRACTION).modifyFlat(id, 0.30f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {


    }

    override fun advance(data: SCData, amount: Float) {
        //data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_provisional_replacements", 1.5f)

    }

    override fun onActivation(data: SCData) {
        //data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_provisional_replacements", 1.5f)

    }

    override fun onDeactivation(data: SCData) {
        //data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).unmodify("sc_provisional_replacements")

    }

}