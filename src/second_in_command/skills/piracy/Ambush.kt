package second_in_command.skills.piracy

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.Sensors
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Ambush : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-25%% detected-at range", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+3 to burn level at which the fleet is considered to be moving slowly*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("*A slow moving fleet is harder to detect in some types of terrain, and can avoid some hazards. Some abilities also make the fleet " +
                "move slowly when activated. A fleet is considered slow-moving at a burn level of half of its slowest ship.", 0f, Misc.getGrayColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {


    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.detectedRangeMod.modifyMult("sc_ambush", 0.75f, "Ambush")
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).modifyFlat("sc_ambush", 3f, "Ambush")
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.detectedRangeMod.modifyMult("sc_ambush", 0.75f, "Ambush")
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).modifyFlat("sc_ambush", 3f, "Ambush")
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.detectedRangeMod.unmodify("sc_ambush")
        data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).unmodify("sc_ambush")
    }

}