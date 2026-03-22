package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.campaign.skills.FluxRegulation
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin
import kotlin.math.max
import kotlin.math.min

class FluxRegulation : SCBaseSkillPlugin() {

    var DISSIPATION_PERCENT: Float = 10f
    var CAPACITY_PERCENT: Float = 10f

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    fun getFleetData(stats: MutableShipStatsAPI?): FleetDataAPI? {
        if (stats == null) {
            if (BaseSkillEffectDescription.isInCampaign()) {
                return Global.getSector().getPlayerFleet().getFleetData()
            }
            return null
        }
        val member = stats.getFleetMember()
        if (member == null) return null
        var data = member.getFleetDataForStats()
        if (data == null) data = member.getFleetData()
        return data
    }

    fun getTotalCombatOP(data: FleetDataAPI, stats: MutableCharacterStatsAPI?): Float {
        var op = 0f
        for (curr in data.getMembersListCopy()) {
            if (curr.isMothballed()) continue
            if (BaseSkillEffectDescription.isCivilian(curr)) continue
            op += getPoints(curr, stats)
        }
        return Math.round(op).toFloat()
    }

    fun getPoints(member: FleetMemberAPI, stats: MutableCharacterStatsAPI?): Float {
        if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
            return member.getDeploymentPointsCost()
        }
        return member.getHullSpec().getOrdnancePoints(stats).toFloat()
    }

    fun getThresholdBasedRoundedBonus(maxBonus: Float, value: Float, threshold: Float): Float {
        var bonus = maxBonus * threshold / max(value, threshold)
        if (bonus > 0 && bonus < 1) bonus = 1f
        if (maxBonus > 1f) {
            if (bonus < maxBonus) {
                bonus = min(bonus, maxBonus - 1f)
            }
            bonus = Math.round(bonus).toFloat()
        }
        return bonus
    }

    fun addOPThresholdInfo(info: TooltipMakerAPI,
                           data: FleetDataAPI,
                           cStats: MutableCharacterStatsAPI?,
                           threshold: Float) {

        val tc = Misc.getTextColor()
        val hc = Misc.getHighlightColor()
        val indent = BaseIntelPlugin.BULLET

        if (BaseSkillEffectDescription.USE_RECOVERY_COST) {
            if (BaseSkillEffectDescription.isInCampaign()) {
                val op = BaseSkillEffectDescription.getTotalCombatOP(data, cStats)
                info.addPara(indent + "Maximum at %s or less total combat ship " + BaseSkillEffectDescription.RECOVERY_COST + ", your fleet's total is %s",
                    0f,
                    tc,
                    hc,
                    "" + threshold.toInt(),
                    "" + Math.round(op))
            } else {
                info.addPara(indent + "Maximum at %s or less total combat ship " + BaseSkillEffectDescription.RECOVERY_COST + " for fleet",
                    0f,
                    tc,
                    hc,
                    "" + threshold.toInt())
            }
            return
        }
        if (BaseSkillEffectDescription.isInCampaign()) {
            val op = BaseSkillEffectDescription.getTotalCombatOP(data, cStats)
            var opStr = "points"
            if (op == 1f) opStr = "point"
            info.addPara(indent + "Maximum at %s or less total combat ship ordnance points in fleet, your fleet has %s " + opStr,
                0f,
                tc,
                hc,
                "" + threshold.toInt(),
                "" + Math.round(op))
        } else {
            info.addPara(indent + "Maximum at %s or less total combat ship ordnance points in fleet",
                0f,
                tc,
                hc,
                "" + threshold.toInt())
        }


    }

    fun addOPThresholdInfo(info: TooltipMakerAPI, data: FleetDataAPI, cStats: MutableCharacterStatsAPI?) {
        addOPThresholdInfo(info, data, cStats, BaseSkillEffectDescription.OP_THRESHOLD)
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        val hc = Misc.getHighlightColor()

        /*tooltip.addPara("+5 maximum flux capacitors and vents for all loadouts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this officer is unassigned, capacitors and vents over the limit are removed", 0f, Misc.getTextColor(), Misc.getHighlightColor())*/


        /*tooltip.addPara("Flux capacity from capacitors is increased by 20", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Flux dissipation from vents is increased by 2", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())*/

        tooltip.addPara("Capacitors and Vents provide more flux capacity and dissipation", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   +10 extra flux capacity per capacitor on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+10")
        tooltip.addPara("   +1 extra flux dissipation per vent on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+1")


        val fleet = data.fleet.fleetData
        val stats = data.fleet.stats;
        //info.addSpacer(5f);
        val data = BaseSkillEffectDescription.getFleetData(null)
        val disBonus: Float = getThresholdBasedRoundedBonus(DISSIPATION_PERCENT,getTotalCombatOP(fleet, data.fleet.commander.stats),240f )
        val capBonus: Float = getThresholdBasedRoundedBonus(CAPACITY_PERCENT,getTotalCombatOP(fleet, data.fleet.commander.stats),240f )

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
        addOPThresholdInfo(tooltip, data, data.fleet.commander.stats)

       /* tooltip.addPara("+10%% flux dissipation", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% flux capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())*/

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var vents = stats!!.variant.numFluxVents
        var caps = stats!!.variant.numFluxCapacitors
        var fluxIncrease = 1f * vents
        var capsIncrease = 10f * caps

        stats.fluxDissipation.modifyFlat(id, fluxIncrease)
        stats.fluxCapacity.modifyFlat(id, capsIncrease)

        stats!!.fluxDissipation.modifyPercent(id, 10f)
        stats!!.fluxCapacity.modifyPercent(id, 10f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData, amount: Float) {
       /* data.commander.stats.maxVentsBonus.modifyFlat("sc_flux_regulation", 5f)
        data.commander.stats.maxCapacitorsBonus.modifyFlat("sc_flux_regulation", 5f)*/
    }

    override fun onDeactivation(data: SCData) {
        /*data.commander.stats.maxVentsBonus.unmodify("sc_flux_regulation")
        data.commander.stats.maxCapacitorsBonus.unmodify("sc_flux_regulation")*/

    }

}