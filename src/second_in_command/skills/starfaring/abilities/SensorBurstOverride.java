package second_in_command.skills.starfaring.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.abilities.SensorBurstAbility;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import second_in_command.SCData;
import second_in_command.SCUtils;

public class SensorBurstOverride extends SensorBurstAbility {

    @Override
    protected void applyEffect(float amount, float level) {

        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        if (entity.isInCurrentLocation()) {
            Global.getSector().getMemoryWithoutUpdate().set(MemFlags.GLOBAL_SENSOR_BURST_JUST_USED_IN_CURRENT_LOCATION, true, 0.1f);
        }
        fleet.getMemoryWithoutUpdate().set(MemFlags.JUST_DID_SENSOR_BURST, true, 0.1f);

//		if (fleet.isPlayerFleet()) {
//			System.out.println("Level: " + level);
//		}

        //float b = fleet.getStats().getDynamic().getValue(Stats.SENSOR_BURST_BURN_PENALTY_MULT);

        //fleet.getStats().getFleetwideMaxBurnMod().modifyMult(getModId(), 1f + (0f - 1f * level) * b, "Active sensor burst");
        //fleet.getStats().getFleetwideMaxBurnMod().modifyMult(getModId(), 1f + (0f - 1f * 1f) * b, "Active sensor burst");
        //fleet.getStats().getFleetwideMaxBurnMod().modifyMult(getModId(), 0, "Active sensor burst");

        fleet.getStats().getSensorRangeMod().modifyFlat(getModId(), SENSOR_RANGE_BONUS * level, "Active sensor burst");
        fleet.getStats().getDetectedRangeMod().modifyFlat(getModId(), DETECTABILITY_RANGE_BONUS * level, "Active sensor burst");

        //fleet.getStats().getAccelerationMult().modifyMult(getModId(), 1f + (ACCELERATION_MULT - 1f) * level);

        boolean dontSlow = false;
        if (fleet.getFleetData() != null) {
            SCData data = SCUtils.getFleetData(fleet);
            if (data.isSkillActive("sc_starfaring_starmapping")) {
                dontSlow = true;
            }
        }

        if (!dontSlow) {
            fleet.goSlowOneFrame();
        }
    }
}
