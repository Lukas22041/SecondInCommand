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

class SuppressionTactics : SCBaseSkillPlugin() {

    var maxPDRange = 100f
    var maxPDTurnRate = 30f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        val pdRangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxPDRange, SCThresholds.ThresholdBonusType.PD_WEAPON_OP)
        val pdTurnBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_turn", maxPDTurnRate, SCThresholds.ThresholdBonusType.PD_WEAPON_OP)

        tooltip.addPara("+50%% autofire aim accuracy for all weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("Affects: all combat ships, including carriers and militarized civilian ships", 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(),
            "all combat ships, including carriers and militarized civilian ships")

        tooltip.addSpacer(10f)

        tooltip.addPara("+${pdRangeBonus.toInt()} PD weapon range (maximum: +${maxPDRange.toInt()})", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+${pdTurnBonus.toInt()}%% PD weapon turn rate (maximum: +${maxPDTurnRate.toInt()}%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        SCThresholds.addPDWeaponOPThresholdInfo(tooltip, data.fleet.fleetData)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.autofireAimAccuracy.modifyFlat(id, 0.5f)

        if (!SCThresholds.isCivilian(stats)) {
            val pdRangeBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_range", maxPDRange, SCThresholds.ThresholdBonusType.PD_WEAPON_OP)
            val pdTurnBonus = SCThresholds.computeAndCacheThresholdBonus(data.fleet.fleetData, data.commander.stats, id + "_turn", maxPDTurnRate, SCThresholds.ThresholdBonusType.PD_WEAPON_OP)

            stats.beamPDWeaponRangeBonus.modifyFlat(id, pdRangeBonus)
            stats.nonBeamPDWeaponRangeBonus.modifyPercent(id, pdTurnBonus)
        }
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        val points = SCThresholds.getPDWeaponPoints(fleet.fleetData)
        val multiplier = (points / SCThresholds.PD_WEAPON_OP_THRESHOLD).coerceIn(0f, 1f)
        return super.getNPCSpawnWeight(fleet) * multiplier
    }
}
