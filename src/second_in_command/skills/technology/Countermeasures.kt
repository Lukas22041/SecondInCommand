package second_in_command.skills.technology

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Countermeasures : SCBaseSkillPlugin() {

    var PER_SHIP_BONUS = 1f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-10%% sensor profile", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Every deployed ship contributes ${PER_SHIP_BONUS.toInt()}%% to the ECM rating* of the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("All ships have slightly improved autofire accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addSpacer(10f)

        var max = ElectronicWarfareScript.BASE_MAXIMUM.toInt()

        tooltip.addPara("*Enemy weapon range is reduced by the total ECM rating of your deployed ships, " +
                "up to a maximum of $max%%. This penalty is reduced by the ratio " +
                "of the enemy ECM rating to yours." + "Does not apply to fighters, affects all weapons including missiles.", 0f, Misc.getGrayColor(), Misc.getHighlightColor(),
            "$max%")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        stats!!.dynamic.getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, PER_SHIP_BONUS)
        stats.autofireAimAccuracy.modifyFlat(id, 0.2f)

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData?, amunt: Float?) {
        data!!.fleet.stats.detectedRangeMod.modifyMult("sc_countermeasures", 0.9f, "Countermeasures")
    }

    override fun onDeactivation(data: SCData?) {
        data!!.fleet.stats.detectedRangeMod.unmodify("sc_countermeasures")
    }

}