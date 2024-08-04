package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin

class PhaseCoilTuning : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all phase ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+120 seconds peak operating time for phase ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30%% top speed and acceleration while phase cloak active", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+50%% sensor strength for phase ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        if (variant.baseOrModSpec().isPhase) {
            stats!!.peakCRDuration.modifyFlat(id, 120f)
            stats!!.sensorStrength.modifyPercent(id, 50f)

            stats.dynamic.getMod(Stats.PHASE_CLOAK_SPEED_MOD).modifyFlat(id, 30f)
            stats.dynamic.getMod(Stats.PHASE_CLOAK_ACCEL_MOD).modifyFlat(id, 30f)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {


    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.baseOrModSpec().isPhase }) return super.getNPCSpawnWeight(fleet)
        return 0f
    }

}