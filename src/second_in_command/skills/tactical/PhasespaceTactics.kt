package second_in_command.skills.tactical

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin

class PhasespaceTactics : SCBaseSkillPlugin() {

    var phaseFluxThresholdIncrease = 30f
    var maxFluxCapacity = 10f
    var maxPhaseSpeed = 10f

    override fun getAffectsString(): String {
        return "all combat phase ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val fluxThreshBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxthresh", phaseFluxThresholdIncrease, SCThresholds.ThresholdBonusType.PHASE_DP)
        val fluxCapBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxcap", maxFluxCapacity, SCThresholds.ThresholdBonusType.PHASE_DP)
        val phaseSpeedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxPhaseSpeed, SCThresholds.ThresholdBonusType.PHASE_DP)

        tooltip.addPara("+${fluxThreshBonus.toInt()}%% to the flux level at which phase speed bottoms out (maximum: +${phaseFluxThresholdIncrease.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${fluxCapBonus.toInt()}%% flux capacity (maximum: +${maxFluxCapacity.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${phaseSpeedBonus.toInt()}%% phase speed (maximum: +${maxPhaseSpeed.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addPhaseOPThresholdInfo(tooltip, data.fleet.fleetData, data.commander.stats)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI?, id: String?) {
        var stats = ship.mutableStats
        if (!SCThresholds.isCivilian(stats) && ship.hullSpec.isPhase) {
            val fluxThreshBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxthresh", phaseFluxThresholdIncrease, SCThresholds.ThresholdBonusType.PHASE_DP)
            val fluxCapBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxcap", maxFluxCapacity, SCThresholds.ThresholdBonusType.PHASE_DP)
            val phaseSpeedBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_speed", maxPhaseSpeed, SCThresholds.ThresholdBonusType.PHASE_DP)

            stats!!.dynamic.getMod(Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).modifyPercent(id, fluxThreshBonus)

            stats.fluxCapacity.modifyPercent(id, fluxCapBonus)
            stats.dynamic.getMod(Stats.PHASE_CLOAK_SPEED_MOD).modifyPercent(id, phaseSpeedBonus)
        }
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.baseOrModSpec().isPhase }) return super.getNPCSpawnWeight(fleet)
        return 0f
    }
}
