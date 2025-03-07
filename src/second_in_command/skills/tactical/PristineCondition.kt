package second_in_command.skills.tactical

import com.fs.starfarer.api.campaign.CampaignFleetAPI
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

class PristineCondition : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships lost in combat have a 20%% percent chance to avoid d-mods", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+5%% combat readiness for every s-mod on the ship (max 15%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var smods = variant.sMods.count()
        var bonus = 0.05f * smods
        bonus = bonus.coerceIn(0f, 0.15f)

        stats!!.maxCombatReadiness.modifyFlat(id, bonus, "Pristine Condition")

        if (data.isNPC && !variant.addAndCheckTag("sc_pristine_condition")) {
            stats.fleetMember.repairTracker.cr += bonus
            stats.fleetMember.repairTracker.cr = MathUtils.clamp(stats.fleetMember.repairTracker.cr, 0f, 1f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun callEffectsFromSeparateSkill(stats: MutableShipStatsAPI?, hullSize: ShipAPI.HullSize?, id: String?) {
          stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.8f)
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.variant.sMods.isNotEmpty() }) return super.getNPCSpawnWeight(fleet)
        return 0f
    }

}