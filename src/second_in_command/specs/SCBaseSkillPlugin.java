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

    /**
     * Chance for a skill to be picked from NPC fleets.
     * This happens after its aptitude has already been choosen for the fleet.
     * Should be kept at around a value of 1, lower to make it rarer, higher to make it more common.
     * @return weight, lower is rarer, 0 is never */
    public Float getNPCSpawnWeight(CampaignFleetAPI fleet) {
        return spec.getNpcSpawnWeight();
    }

    public abstract String getAffectsString();

    public abstract void addTooltip(SCData data, TooltipMakerAPI tooltip);

    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) { }

    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) { }

    public void applyEffectsToFighterSpawnedByShip(SCData data, ShipAPI fighter, ShipAPI ship, String id) { }



    /**Non-ship specific campaign advance*/
    public void advance(SCData data, Float amunt) { }

    /**Ship specific combat advance*/
    public void advanceInCombat(SCData data, ShipAPI ship, Float amount) { }

    /**Called when the skill is acquired and if the officer is re-assigned. Also may be called in other scenarios aslong as the skill is active*/
    public void onActivation(SCData data) { }

    /**Called when the corrosponding officer is un-assigned */
    public void onDeactivation(SCData data) { }








    /**Might be useful for some rare effects that will not work when executed from the normal methods. This is for player fleet effects only.
     * It's executed from an invisible skill added to the player, which will be called for every ship in the fleet
     * It can also be useful in cases where the ship is for some reason not considered part of the player fleet for that moment, which can be during ship recovery
     * You should not use this unless you really need it*/
    public void callEffectsFromSeparateSkill(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

    }











    /**Ship specific campaign advance
     * @deprecated
     * Deprecated and no longer called due to barely any use, but high performance impact. Use the advance function and iterate over fleet members instead.
     * */
    @Deprecated()
    public void advanceInCampaign(SCData data, FleetMemberAPI member, Float amount) { }
}
