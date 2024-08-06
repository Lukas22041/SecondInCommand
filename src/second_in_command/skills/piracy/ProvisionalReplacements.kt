package second_in_command.skills.piracy

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.skills.HullRestoration
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ProvisionalReplacements : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All ships are much more likely to be recoverable if lost in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-6%% monthly supply usage per d-mod on the ship", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The maximum effect is reached at 5 d-mods", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var dmods = 0

        var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

        var hmods = variant.permaMods
        for (hmod in hmods) {
            if (dmodSpecs.map { it.id }.contains(hmod)) {
                dmods += 1
            }
        }

        var bonus = 0.06f * dmods
        bonus = bonus.coerceIn(0f, 0.30f)

        stats!!.suppliesPerMonth.modifyMult(id, 1-bonus, "Provisional Replacements")
        stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat(id, 1.5f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

}