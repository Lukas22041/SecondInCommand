package second_in_command.skills.scavenging

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class HyperspatialDrifter : SCBaseSkillPlugin() {

    companion object {
       /* var MIN_DISTANCE = 0.1
        var MAX_DISTANCE = 3f*/
        var SCRAP_PER_LIGHTYEAR = 1f
        var SCRAP_EXTRA_PER_SALVAGE_MIN = 3f
        var SCRAP_EXTRA_PER_SALVAGE_MAX = 5f
    }

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Your fleet recovers an additional flat 3%%-5%% Scrap on average from salvaging actions", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Your fleet salvages small pieces of debris that it finds along its way within hyperspace for Scrap", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - It recovers around ${SCRAP_PER_LIGHTYEAR.toInt()}%% of Scrap per 2 light-years traveled on average*", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${SCRAP_PER_LIGHTYEAR.toInt()}%", "2 light-years")

        tooltip.addSpacer(10f)

        tooltip.addPara("*This effect is disabled while either one of the effects from \"Makeshift Measures\" is active. ", 0f, Misc.getGrayColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}