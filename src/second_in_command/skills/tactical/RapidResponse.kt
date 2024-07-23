package second_in_command.skills.tactical

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin

class RapidResponse : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(tooltip: TooltipMakerAPI) {

        tooltip.addPara("+1 increased maximum burn for ships with less than 9 burn speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+15%% maneuverability for the fleet outside of combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (stats!!.maxBurnLevel.modifiedValue < 9) {
            stats!!.maxBurnLevel.modifyFlat(id,1f)
        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(amount: Float) {
        Global.getSector().playerFleet.stats.accelerationMult.modifyMult("sc_rapid_response", 1.15f, "Rapid Response")

    }

    override fun onDeactivation() {
        Global.getSector().playerFleet.stats.accelerationMult.unmodify("sc_rapid_response")

    }

}