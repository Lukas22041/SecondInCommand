package second_in_command.skills.engineering

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.SCUtils.addAndCheckTag
import second_in_command.specs.SCBaseSkillPlugin

class PrepareForTheWorst : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+10%% maximum combat readiness for all ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% hull & armor repair rate outside of combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+4%% flat increase to the combat readiness recovered per day", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var cr = 0.10f

        stats.maxCombatReadiness.modifyFlat(id, cr, "Prepare for the worst")
        stats.repairRatePercentPerDay.modifyPercent(id, 30f)
        stats.baseCRRecoveryRatePercentPerDay.modifyFlat(id, 4f)

        if (data.isNPC && !variant.addAndCheckTag("sc_prepare_for_the_worst")) {
            stats.fleetMember.repairTracker.cr += cr
            stats.fleetMember.repairTracker.cr = MathUtils.clamp(stats.fleetMember.repairTracker.cr, 0f, 1f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}