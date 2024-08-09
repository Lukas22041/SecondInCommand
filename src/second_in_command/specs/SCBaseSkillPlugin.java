package second_in_command.specs;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import second_in_command.SCData;

/**Base Plugin for Skills.
 * Handles similar to hullmod plugins.
 * Do not store variables in the class itself. It will cause leaks.*/
public abstract class SCBaseSkillPlugin {

    public  SCSkillSpec spec;

    public final String getId() {
        return spec.getId();
    }

    public final String getIconPath() {
        return spec.getIconPath();
    }

    public String getName() {
        return spec.getName();
    }

    public Float getNPCSpawnWeight(CampaignFleetAPI fleet) {
        return spec.getNpcSpawnWeight();
    }

    public abstract String getAffectsString();

    public abstract void addTooltip(SCData data, TooltipMakerAPI tooltip);

    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) { }

    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) { }

    public void applyEffectsToFighterSpawnedByShip(SCData data, ShipAPI fighter, ShipAPI ship, String id) { }

    public void advanceInCampaign(SCData data, FleetMemberAPI member, Float amount) { }

    /**Non-ship specific campaign advance*/
    public void advance(SCData data, Float amunt) { }

    public void advanceInCombat(SCData data, ShipAPI ship, Float amount) { }

    /**Called when the skill is acquired and if the officer is re-assigned. Also may be called in other scenarios aslong as the skill is active*/
    public void onActivation(SCData data) { }

    /**Called when the corrosponding officer is un-assigned */
    public void onDeactivation(SCData data) { }
}
