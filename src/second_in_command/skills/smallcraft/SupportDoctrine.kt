package second_in_command.skills.smallcraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.*
import com.fs.starfarer.api.impl.campaign.skills.SupportDoctrine
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SupportDoctrine : SCBaseSkillPlugin() {

    var DP_REDUCTION_MAX = 10f
    var DP_REDUCTION = 0.2f

    override fun getAffectsString(): String {
        return "all ships without officers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Gain non-elite Helmsmanship, Damage Control, Combat Endurance, and Ordnance Expertise", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Deployment point cost reduced by 20%% or 10, whichever is less", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var member = stats!!.fleetMember ?: return
        if (member.captain == null || member.captain.isDefault) {

            //Apply Skill Effects
            Helmsmanship.Level1().apply(stats, hullSize, id, 1f)
            Helmsmanship.Level2().apply(stats, hullSize, id, 1f)

            DamageControl.Level2().apply(stats, hullSize, id, 1f)
            DamageControl.Level3().apply(stats, hullSize, id, 1f)
            DamageControl.Level4().apply(stats, hullSize, id, 1f)

            CombatEndurance.Level1().apply(stats, hullSize, id, 1f)
            CombatEndurance.Level2().apply(stats, hullSize, id, 1f)
            CombatEndurance.Level3().apply(stats, hullSize, id, 1f)

            OrdnanceExpertise.Level1().apply(stats, hullSize, id, 1f)

            //DP Reduction
            val baseCost = stats.suppliesToRecover.baseValue
            val reduction = Math.min(DP_REDUCTION_MAX, baseCost * DP_REDUCTION)

            if (stats.fleetMember == null || stats.fleetMember.variant == null || !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTERFACE) && !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTEGRATOR)) {
                stats.dynamic.getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(SupportDoctrine.SUPPORT_DOCTRINE_DP_REDUCTION_ID, -reduction)
            }
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}