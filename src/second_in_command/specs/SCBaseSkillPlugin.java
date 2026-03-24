package second_in_command.specs;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.hullmods.Automated;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;

import java.awt.Color;

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


    // -------------------------------------------------------------------------
    // Threshold system
    // -------------------------------------------------------------------------



    private final static boolean USE_RECOVERY_COST = true;
    private final static String RECOVERY_COST = "deployment point cost";
    private final static float FIGHTER_BAYS_THRESHOLD = 8f;
    private final static float OP_THRESHOLD = 240f;
    private final static float OP_LOW_THRESHOLD = 120f;
    private final static float OP_ALL_LOW_THRESHOLD = 120f;
    private final static float OP_ALL_THRESHOLD = 240f;
    private final static float PHASE_OP_THRESHOLD = 40f;
    private final static float MILITARIZED_OP_THRESHOLD = 5f;

    public enum ThresholdBonusType {
        OP,
        OP_LOW,
        OP_ALL,
        OP_ALL_LOW,
        MILITARIZED_OP,
        PHASE_OP,
        FIGHTER_BAYS,
        AUTOMATED_POINTS,
    }

    // --- Core computation ---

    protected float computeAndCacheThresholdBonus(MutableShipStatsAPI stats,
                                                   String key, float maxBonus, ThresholdBonusType type) {
        FleetDataAPI data = getFleetData(stats);
        MutableCharacterStatsAPI cStats = getCommanderStats(stats);
        return computeAndCacheThresholdBonus(data, cStats, key, maxBonus, type);
    }

    protected float computeAndCacheThresholdBonus(FleetDataAPI data, MutableCharacterStatsAPI cStats,
                                                   String key, float maxBonus, ThresholdBonusType type) {
        if (data == null) return maxBonus;
        if (cStats.getFleet() == null) return maxBonus;

        Float bonus = (Float) data.getCacheClearedOnSync().get(key);
        if (bonus != null) return bonus;

        float currValue = 0f;
        float threshold = 1f;

        if (type == ThresholdBonusType.FIGHTER_BAYS) {
            currValue = getNumFighterBays(data);
            threshold = FIGHTER_BAYS_THRESHOLD;
        } else if (type == ThresholdBonusType.OP) {
            currValue = getTotalCombatOP(data, cStats);
            threshold = OP_THRESHOLD;
        } else if (type == ThresholdBonusType.OP_LOW) {
            currValue = getTotalCombatOP(data, cStats);
            threshold = OP_LOW_THRESHOLD;
        } else if (type == ThresholdBonusType.OP_ALL_LOW) {
            currValue = getTotalOP(data, cStats);
            threshold = OP_ALL_LOW_THRESHOLD;
        } else if (type == ThresholdBonusType.OP_ALL) {
            currValue = getTotalOP(data, cStats);
            threshold = OP_ALL_THRESHOLD;
        } else if (type == ThresholdBonusType.MILITARIZED_OP) {
            currValue = getMilitarizedOP(data, cStats);
            threshold = MILITARIZED_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.PHASE_OP) {
            currValue = getPhaseOP(data, cStats);
            threshold = PHASE_OP_THRESHOLD;
        } /*else if (type == ThresholdBonusType.AUTOMATED_POINTS) {
            currValue = getAutomatedPoints(data, cStats);
            threshold = AUTOMATED_POINTS_THRESHOLD;
        }*/

        bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold);
        data.getCacheClearedOnSync().put(key, bonus);
        return bonus;
    }

    protected float getThresholdBasedBonus(float maxBonus, float value, float threshold) {
        return maxBonus * threshold / Math.max(value, threshold);
    }

    protected float getThresholdBasedRoundedBonus(float maxBonus, float value, float threshold) {
        float bonus = maxBonus * threshold / Math.max(value, threshold);
        if (bonus > 0 && bonus < 1) bonus = 1;
        if (maxBonus > 1f) {
            if (bonus < maxBonus) {
                bonus = Math.min(bonus, maxBonus - 1f);
            }
            bonus = (float) Math.round(bonus);
        }
        return bonus;
    }

    // --- Fleet data helpers ---

    protected boolean isInCampaign() {
        return Global.getCurrentState() == GameState.CAMPAIGN &&
               Global.getSector() != null &&
               Global.getSector().getPlayerFleet() != null;
    }

    protected FleetDataAPI getFleetData(MutableShipStatsAPI stats) {
        if (stats == null) {
            if (isInCampaign()) {
                return Global.getSector().getPlayerFleet().getFleetData();
            }
            return null;
        }
        FleetMemberAPI member = stats.getFleetMember();
        if (member == null) return null;
        FleetDataAPI data = member.getFleetDataForStats();
        if (data == null) data = member.getFleetData();
        return data;
    }

    protected MutableCharacterStatsAPI getCommanderStats(MutableShipStatsAPI stats) {
        if (stats == null) {
            if (isInCampaign()) {
                return Global.getSector().getPlayerStats();
            }
            return null;
        }
        FleetMemberAPI member = stats.getFleetMember();
        if (member == null) return null;
        PersonAPI commander = member.getFleetCommanderForStats();
        if (commander == null) {
            boolean orig = false;
            if (member.getFleetData() != null) {
                orig = member.getFleetData().isForceNoSync();
                member.getFleetData().setForceNoSync(true);
            }
            commander = member.getFleetCommander();
            if (member.getFleetData() != null) {
                member.getFleetData().setForceNoSync(orig);
            }
        }
        if (commander != null) {
            return commander.getStats();
        }
        return null;
    }

    // --- OP / points tallies ---

    protected float getPoints(FleetMemberAPI member, MutableCharacterStatsAPI stats) {
        if (USE_RECOVERY_COST) {
            return member.getDeploymentPointsCost();
        }
        return member.getHullSpec().getOrdnancePoints(stats);
    }

    protected float getTotalOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    protected float getTotalCombatOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    protected float getPhaseOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (curr.isPhaseShip()) {
                if (isCivilian(curr)) continue;
                op += getPoints(curr, stats);
            }
        }
        return Math.round(op);
    }

    protected float getMilitarizedOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!isMilitarized(curr)) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    protected float getNumFighterBays(FleetDataAPI data) {
        if (data == null) return FIGHTER_BAYS_THRESHOLD;
        float bays = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            bays += getNumBaysIncludingModules(curr);
        }
        return bays;
    }

    protected float getNumBaysIncludingModules(FleetMemberAPI member) {
        float bays = 0f;
        bays += member.getNumFlightDecks();
        if (member.getVariant().getModuleSlots() != null) {
            for (String slotId : member.getVariant().getModuleSlots()) {
                if (slotId == null) continue;
                ShipVariantAPI variant = member.getVariant().getModuleVariant(slotId);
                if (variant == null) continue;
                bays += variant.getHullSpec().getFighterBays();
            }
        }
        return bays;
    }

    protected float getAutomatedPoints(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float points = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!Misc.isAutomated(curr)) continue;
            if (Automated.isAutomatedNoPenalty(curr)) continue;
            float mult = 1f;
            points += curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE);
            mult = curr.getCaptain().getMemoryWithoutUpdate().getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT);
            if (mult == 0) mult = 1;
            points += Math.round(getPoints(curr, stats) * mult);
        }
        return Math.round(points);
    }

    // --- Ship classification helpers ---

    protected boolean isCivilian(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return isCivilian(stats.getFleetMember());
    }

    protected boolean isCivilian(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getVariant() != null &&
               ((stats.getVariant().hasHullMod(HullMods.CIVGRADE) &&
                 !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) ||
                (!stats.getVariant().hasHullMod(HullMods.CIVGRADE) &&
                  stats.getVariant().getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)));
    }

    protected boolean isMilitarized(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return isMilitarized(stats.getFleetMember());
    }

    protected boolean isMilitarized(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getVariant() != null &&
               stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS);
    }

    protected boolean hasFighterBays(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return hasFighterBays(stats.getFleetMember());
    }

    protected boolean hasFighterBays(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getNumFighterBays().getModifiedInt() > 0;
    }

    // --- Tooltip info methods ---

    protected void addFighterBayThresholdInfo(TooltipMakerAPI info, FleetDataAPI data) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (isInCampaign()) {
            int bays = Math.round(getNumFighterBays(data));
            String baysStr = bays == 1 ? "fighter bay" : "fighter bays";
            info.addPara(indent + "Maximum at %s or less fighter bays in fleet, your fleet has %s " + baysStr,
                    0f, tc, hc,
                    "" + (int) FIGHTER_BAYS_THRESHOLD,
                    "" + bays);
        } else {
            info.addPara(indent + "Maximum at %s or less fighter bays in fleet",
                    0f, tc, hc,
                    "" + (int) FIGHTER_BAYS_THRESHOLD);
        }
    }

    protected void addOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        addOPThresholdInfo(info, data, cStats, OP_THRESHOLD);
    }

    protected void addOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getTotalCombatOP(data, cStats);
                info.addPara(indent + "Maximum at %s or less total combat ship " + RECOVERY_COST + ", your fleet's total is %s",
                        0f, tc, hc,
                        "" + (int) threshold,
                        "" + (int) Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total combat ship " + RECOVERY_COST + " for fleet",
                        0f, tc, hc,
                        "" + (int) threshold);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getTotalCombatOP(data, cStats);
            String opStr = op == 1 ? "point" : "points";
            info.addPara(indent + "Maximum at %s or less total combat ship ordnance points in fleet, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) threshold,
                    "" + (int) Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total combat ship ordnance points in fleet",
                    0f, tc, hc,
                    "" + (int) threshold);
        }
    }

    protected void addOPThresholdAll(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getTotalOP(data, cStats);
                info.addPara(indent + "Maximum at %s or less total " + RECOVERY_COST + ", your fleet's total is %s",
                        0f, tc, hc,
                        "" + (int) threshold,
                        "" + (int) Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total " + RECOVERY_COST + " for fleet",
                        0f, tc, hc,
                        "" + (int) threshold);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getTotalOP(data, cStats);
            String opStr = op == 1 ? "point" : "points";
            info.addPara(indent + "Maximum at %s or less total ordnance points in fleet, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) threshold,
                    "" + (int) Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total ordnance points in fleet",
                    0f, tc, hc,
                    "" + (int) threshold);
        }
    }

    protected void addPhaseOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getPhaseOP(data, cStats);
                info.addPara(indent + "Maximum at %s or less total combat phase ship " + RECOVERY_COST + ", your fleet's total is %s",
                        0f, tc, hc,
                        "" + (int) PHASE_OP_THRESHOLD,
                        "" + (int) Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total combat phase ship " + RECOVERY_COST + " for fleet",
                        0f, tc, hc,
                        "" + (int) PHASE_OP_THRESHOLD);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getPhaseOP(data, cStats);
            String opStr = op == 1 ? "point" : "points";
            info.addPara(indent + "Maximum at %s or less total combat phase ship ordnance points in fleet, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) PHASE_OP_THRESHOLD,
                    "" + (int) Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total combat phase ship ordnance points in fleet",
                    0f, tc, hc,
                    "" + (int) PHASE_OP_THRESHOLD);
        }
    }

    /*protected void addAutomatedThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getAutomatedPoints(data, cStats);
                info.addPara(indent + "Maximum at %s or less total automated ship points*, your fleet's total is %s ",
                        0f, tc, hc,
                        "" + (int) AUTOMATED_POINTS_THRESHOLD,
                        "" + (int) Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total automated ship points* for fleet",
                        0f, tc, hc,
                        "" + (int) AUTOMATED_POINTS_THRESHOLD);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getAutomatedPoints(data, cStats);
            String opStr = op == 1 ? "point" : "points";
            info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) AUTOMATED_POINTS_THRESHOLD,
                    "" + (int) Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total automated ship points* in fleet",
                    0f, tc, hc,
                    "" + (int) AUTOMATED_POINTS_THRESHOLD);
        }
    }*/

    protected void addMilitarizedOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (USE_RECOVERY_COST) {
            if (isInCampaign()) {
                float op = getMilitarizedOP(data, cStats);
                info.addPara(indent + "Maximum at %s or less total " + RECOVERY_COST + " for ships with Militarized Subsystems, your fleet's total is %s",
                        0f, tc, hc,
                        "" + (int) MILITARIZED_OP_THRESHOLD,
                        "" + (int) Math.round(op));
            } else {
                info.addPara(indent + "Maximum at %s or less total " + RECOVERY_COST + " for ships with Militarized Subsystems for fleet",
                        0f, tc, hc,
                        "" + (int) MILITARIZED_OP_THRESHOLD);
            }
            return;
        }
        if (isInCampaign()) {
            float op = getMilitarizedOP(data, cStats);
            String opStr = op == 1 ? "point" : "points";
            info.addPara(indent + "Maximum at %s or less total ordnance points for ships with Militarized Subsystems, your fleet has %s " + opStr,
                    0f, tc, hc,
                    "" + (int) MILITARIZED_OP_THRESHOLD,
                    "" + (int) Math.round(op));
        } else {
            info.addPara(indent + "Maximum at %s or less total ordnance points for ships with Militarized Subsystems",
                    0f, tc, hc,
                    "" + (int) MILITARIZED_OP_THRESHOLD);
        }
    }
}
