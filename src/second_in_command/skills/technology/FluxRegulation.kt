package second_in_command.skills.technology

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.FluxRegulation
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCBaseSkillPlugin

class FluxRegulation : SCBaseSkillPlugin() {

    var DISSIPATION_PERCENT: Float = 10f
    var CAPACITY_PERCENT: Float = 10f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        val hc = Misc.getHighlightColor()

        tooltip.addPara("Capacitors and Vents provide more flux capacity and dissipation", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   +10 extra flux capacity per capacitor on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+10")
        tooltip.addPara("   +1 extra flux dissipation per vent on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+1")

        val fleet = data.fleet.fleetData
        val fleetData = SCThresholds.getFleetData(null)
        val disBonus: Float = SCThresholds.getThresholdBasedRoundedBonus(DISSIPATION_PERCENT, SCThresholds.getTotalCombatOP(fleet, data.fleet.commander.stats), SCThresholds.OP_THRESHOLD)
        val capBonus: Float = SCThresholds.getThresholdBasedRoundedBonus(CAPACITY_PERCENT, SCThresholds.getTotalCombatOP(fleet, data.fleet.commander.stats), SCThresholds.OP_THRESHOLD)

        val opad = 10f
        val c = Misc.getBasePlayerColor()
        tooltip.addPara("Affects: %s",
            opad + 5f,
            Misc.getGrayColor(),
            c,
            "all combat ships, including carriers and militarized civilian ships")

        tooltip.addPara("+%s flux dissipation for combat ships (maximum: %s)",
            opad,
            hc,
            hc,
            "" + disBonus.toInt() + "%",
            "" + FluxRegulation.DISSIPATION_PERCENT.toInt() + "%")

        tooltip.addPara("+%s flux capacity for combat ships (maximum: %s)",
            0f,
            hc,
            hc,
            "" + capBonus.toInt() + "%",
            "" + FluxRegulation.CAPACITY_PERCENT.toInt() + "%")
        SCThresholds.addOPThresholdInfo(tooltip, fleetData, data.fleet.commander.stats)
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        val vents = stats!!.variant.numFluxVents
        val caps = stats!!.variant.numFluxCapacitors

        stats.fluxDissipation.modifyFlat(id, 1f * vents)
        stats.fluxCapacity.modifyFlat(id, 10f * caps)

        val disBonus = SCThresholds.computeAndCacheThresholdBonus(stats, id + "_dis", DISSIPATION_PERCENT, SCThresholds.ThresholdBonusType.DP)
        val capBonus = SCThresholds.computeAndCacheThresholdBonus(stats, id + "_cap", CAPACITY_PERCENT, SCThresholds.ThresholdBonusType.DP)

        stats.fluxDissipation.modifyPercent(id, disBonus)
        stats.fluxCapacity.modifyPercent(id, capBonus)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
    }

    override fun advance(data: SCData, amount: Float) {
    }

    override fun onDeactivation(data: SCData) {
    }

}
