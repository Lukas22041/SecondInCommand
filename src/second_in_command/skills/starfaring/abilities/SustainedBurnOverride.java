package second_in_command.skills.starfaring.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberViewAPI;
import com.fs.starfarer.api.impl.campaign.abilities.SustainedBurnAbility;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import second_in_command.SCData;
import second_in_command.SCUtils;

public class SustainedBurnOverride extends SustainedBurnAbility {

    public static float ACCELERATION_MULT = 0.2f;
    public static float ACCELERATION_MULT_SKILL = 0.5f;

    @Override
    protected void applyEffect(float amount, float level) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        //System.out.println("Level: " + level);
        //level = 0.01f;

        if (level > 0 && !fleet.isAIMode() && fleet.getCargo().getFuel() <= 0 &&
                fleet.getContainingLocation() != null && fleet.getContainingLocation().isHyperspace()) {
            deactivate();
            return;
        }

        fleet.getMemoryWithoutUpdate().set(SB_NO_STOP, true, 0.3f);

        if (level > 0 && level < 1 && amount > 0 && !fleet.getMemoryWithoutUpdate().is(SB_NO_SLOW, true)) {
            float activateSeconds = getActivationDays() * Global.getSector().getClock().getSecondsPerDay();
            float speed = fleet.getVelocity().length();
            float acc = Math.max(speed, 200f)/activateSeconds + fleet.getAcceleration();
            float ds = acc * amount;
            if (ds > speed) ds = speed;
            Vector2f dv = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(fleet.getVelocity()));
            dv.scale(ds);
            fleet.setVelocity(fleet.getVelocity().x - dv.x, fleet.getVelocity().y - dv.y);
            return;
        }

        //fleet.getStats().getSensorRangeMod().modifyMult(getModId(), 1f + (SENSOR_RANGE_MULT - 1f) * level, "Sustained burn");
        fleet.getStats().getDetectedRangeMod().modifyPercent(getModId(), DETECTABILITY_PERCENT * level, "Sustained burn");

        //int burnModifier = (int)(MAX_BURN_MOD * level) - (int)(INITIAL_BURN_PENALTY * (1f - level));
        //int burnModifier = (int)(MAX_BURN_MOD * level);
        int burnModifier = 0;
        float burnMult = 1f;

        float b = fleet.getStats().getDynamic().getValue(Stats.SUSTAINED_BURN_BONUS, 0f);
        //burnModifier = (int)((MAX_BURN_MOD + b) * level);
        burnModifier = (int)((b) * level);

//		if (level > 0.5f) {
//			burnModifier = (int)(MAX_BURN_MOD * (level - 0.5f) / 0.5f);
//		} else {
//			//burnModifier = -1 * (int)(INITIAL_BURN_PENALTY * (1f - level / 0.5f));
//			burnMult = 1f + ((INITIAL_BURN_PENALTY - 1f) * (1f - level / 0.5f));
//		}
        fleet.getStats().getFleetwideMaxBurnMod().modifyFlat(getModId(), burnModifier, "Sustained burn");
        fleet.getStats().getFleetwideMaxBurnMod().modifyMult(getModId(), burnMult, "Sustained burn");
        fleet.getStats().getFleetwideMaxBurnMod().modifyPercent(getModId(), MAX_BURN_PERCENT, "Sustained burn");


        float accImpact = 0f;
        float burn = Misc.getBurnLevelForSpeed(fleet.getVelocity().length());
        if (burn > 1) {
            float dir = Misc.getDesiredMoveDir(fleet);
//			if (fleet.isPlayerFleet()) {
//				System.out.println("DIR: " + dir);
//			}
            float velDir = Misc.getAngleInDegrees(fleet.getVelocity());
            float diff = Misc.getAngleDiff(dir, velDir);
            //float pad = 90f;
            float pad = 120f;
            diff -= pad;
            if (diff < 0) diff = 0;
            accImpact = 1f - 0.5f * Math.min(1f, (diff / (180f - pad)));
        }



        float mult = ACCELERATION_MULT;

        if (fleet.getFleetData() != null) {
            SCData data = SCUtils.getFleetData(fleet);
            if (data.isSkillActive("sc_starfaring_reactive_burn")) {
                mult = ACCELERATION_MULT_SKILL;

                data.getFleet().getStats().addTemporaryModMult(0.1f, "sc_reactive_burn", "Reactive Burn", 0f, data.getFleet().getStats().getFuelUseHyperMult());
                data.getFleet().getStats().addTemporaryModMult(0.1f, "sc_reactive_burn", "Reactive Burn", 0f, data.getFleet().getStats().getFuelUseNormalMult());


            }
        }


        fleet.getStats().getAccelerationMult().modifyMult(getModId(), 1f - (1f - mult) * accImpact);


        for (FleetMemberViewAPI view : fleet.getViews()) {
            //view.getContrailColor().shift(getModId(), new Color(50,50,50,155), 1f, 1f, .5f);
            view.getContrailColor().shift(getModId(), view.getEngineColor().getBase(), 1f, 1f, 0.5f * level);
            view.getEngineGlowSizeMult().shift(getModId(), 1.5f, 1f, 1f, 1f * level);
            view.getEngineHeightMult().shift(getModId(), 3f, 1f, 1f, 1f * level);
            view.getEngineWidthMult().shift(getModId(), 2f, 1f, 1f, 1f * level);
        }


        if (level <= 0) {
            cleanupImpl();
        }
    }

}
