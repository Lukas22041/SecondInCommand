package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Strings
import com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class ElectronicWarfare : SCBaseSkillPlugin() {

    var PER_SHIP_BONUS = 1.5f

    var CAP_RANGE = 300f
    var CAP_RATE = 3f

    override fun getAffectsString(): String {
        return "all ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Every deployed ship contributes $PER_SHIP_BONUS%% to the ECM rating* of the fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Combat objectives are captured 3${Strings.X} faster and can be captured from 300 units further away", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        var max = ElectronicWarfareScript.BASE_MAXIMUM.toInt()

        tooltip.addPara("*Enemy weapon range is reduced by the total ECM rating of your deployed ships, " +
                "up to a maximum of $max%%. This penalty is reduced by the ratio " +
                "of the enemy ECM rating to yours." + "Does not apply to fighters, affects all weapons including missiles.", 0f, Misc.getGrayColor(), Misc.getHighlightColor(),
        "$max%")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        //if (Misc.isAutomated(stats)) {

            stats!!.dynamic.getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, PER_SHIP_BONUS)

            stats!!.dynamic.getMod(Stats.SHIP_OBJECTIVE_CAP_RANGE_MOD).modifyFlat(id, CAP_RANGE)
            stats.dynamic.getStat(Stats.SHIP_OBJECTIVE_CAP_RATE_MULT).modifyMult(id, CAP_RATE)
        //}
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}