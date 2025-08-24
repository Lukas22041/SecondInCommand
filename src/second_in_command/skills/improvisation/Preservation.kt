package second_in_command.skills.improvisation

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.hullmods.DegradedShields
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Preservation : SCBaseSkillPlugin() {

    companion object {

        var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }.map { it.id }

        fun reapplyDmods(variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI) {
            if (variant.hasTag("sc_applied_dmods_this_frame")) return
            variant.addTag("sc_applied_dmods_this_frame")

            var hmods = variant.permaMods
            for (hmod in hmods) {
                if (dmodSpecs.contains(hmod)) {

                    var spec = Global.getSettings().getHullModSpec(hmod) ?: continue
                    var plugin = spec.effect
                    plugin.applyEffectsBeforeShipCreation(hullSize, stats, hmod)
                }
            }

            variant.removeTag("sc_applied_dmods_this_frame")
        }
    }

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All of your ships are more likely to be recoverable if lost in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Reduces most negative effects of d-mods by 25%%*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("*This effect stacks multiplicatively with others of the same kind", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "multiplicatively")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        /*if (data.isPlayer) {
            stats.dynamic.getStat(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyMult(id, 1.5f)
        }*/


        stats.dynamic.getStat(Stats.DMOD_EFFECT_MULT).modifyMult(id, 0.75f)
        reapplyDmods(variant, hullSize, stats)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI, amount: Float?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_preservation", 1.5f)

    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_preservation", 1.5f)

    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).unmodify("sc_preservation")
    }


}