package second_in_command.skills.tactical

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class BulwarkTactics : SCBaseSkillPlugin() {

    var maxArmorBonus = 10f
    var maxFluxCapBonus = 20f
    var maxPptBonus = 25f

    override fun getAffectsString(): String {
        return "all combat cruisers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val armorBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_armor", maxArmorBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)
        val fluxCapBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxcap", maxFluxCapBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)
        val pptBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_ppt", maxPptBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)

        tooltip.addPara("+${armorBonus.toInt()}%% armor rating (maximum: +${maxArmorBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${fluxCapBonus.toInt()}%% flux capacity (maximum: +${maxFluxCapBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${pptBonus.toInt()}%% peak performance time (maximum: +${maxPptBonus.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addCruiserDPThresholdInfo(tooltip, data.fleet.fleetData, data.commander.stats)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (SCThresholds.isCivilian(stats)) return
        if (hullSize != ShipAPI.HullSize.CRUISER) return

        val armorBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_armor", maxArmorBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)
        val fluxCapBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_fluxcap", maxFluxCapBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)
        val pptBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_ppt", maxPptBonus, SCThresholds.ThresholdBonusType.CRUISER_DP)

        stats!!.armorBonus.modifyPercent(id, armorBonus)
        stats.fluxCapacity.modifyPercent(id, fluxCapBonus)
        stats.peakCRDuration.modifyPercent(id, pptBonus)
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        val dp = SCThresholds.getCruiserDP(fleet.fleetData, fleet.commander.stats)
        val multiplier = (dp / SCThresholds.CRUISER_DP_THRESHOLD).coerceIn(0f, 1f)
        return super.getNPCSpawnWeight(fleet) * multiplier
    }
}
