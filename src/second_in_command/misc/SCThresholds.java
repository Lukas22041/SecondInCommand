package second_in_command.misc;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.hullmods.Automated;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;

/**
 * Static utility class for fleet threshold calculations and threshold-based bonus scaling.
 * All methods and constants are public static for easy access from any skill or aptitude.
 */
public class SCThresholds {

    private SCThresholds() {}

    // -------------------------------------------------------------------------
    // Internal settings
    // -------------------------------------------------------------------------

    private static final boolean USE_RECOVERY_COST = true;
    private static final String RECOVERY_COST = "deployment point cost";

    // -------------------------------------------------------------------------
    // Threshold constants
    // -------------------------------------------------------------------------

    public static final float FIGHTER_BAYS_THRESHOLD = 8f;
    public static final float OP_THRESHOLD = 240f; //Flux reg
    public static final float OP_ALL_LOW_THRESHOLD = 120f;
    public static final float OP_ALL_THRESHOLD = 240f;
    public static final float MILITARIZED_OP_THRESHOLD = 5f;

    // Tactical aptitude thresholds

    public static final float DP_LOW_THRESHOLD = 120f;
    public static final float FIGHTER_BAYS_COMBAT_THRESHOLD = 12f;
    public static final float FRIGATE_DESTROYER_DP_THRESHOLD = 90f;
    public static final float CRUISER_DP_THRESHOLD = 90f;
    public static final float CAPITAL_DP_THRESHOLD = 90f;
    public static final float MISSILE_WEAPON_OP_THRESHOLD = 90f;
    public static final float PD_WEAPON_OP_THRESHOLD = 90f;
    public static final float PHASE_OP_THRESHOLD = 90f;


    // -------------------------------------------------------------------------
    // Threshold type enum
    // -------------------------------------------------------------------------

    public enum ThresholdBonusType {

        DP, //Flux regulation
        FIGHTER_BAYS,
        OP_ALL,
        OP_ALL_LOW,
        MILITARIZED_OP,
        // Tactical aptitude threshold types (civilian ships excluded from all)
        PHASE_DP,
        DP_LOW,
        FIGHTER_BAYS_COMBAT,
        FRIGATE_DESTROYER_DP,
        CRUISER_DP,
        CAPITAL_DP,
        MISSILE_WEAPON_OP,
        PD_WEAPON_OP,
    }

    // -------------------------------------------------------------------------
    // Compute & cache
    // -------------------------------------------------------------------------

    public static float computeAndCacheThresholdBonus(MutableShipStatsAPI stats,
                                                      String key, float maxBonus, ThresholdBonusType type) {
        FleetDataAPI data = getFleetData(stats);
        MutableCharacterStatsAPI cStats = getCommanderStats(stats);
        return computeAndCacheThresholdBonus(data, cStats, key, maxBonus, type);
    }

    public static float computeAndCacheThresholdBonus(FleetDataAPI data, MutableCharacterStatsAPI cStats,
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
        } else if (type == ThresholdBonusType.DP) {
            currValue = getTotalCombatOP(data, cStats);
            threshold = OP_THRESHOLD;
        } else if (type == ThresholdBonusType.DP_LOW) {
            currValue = getTotalCombatOP(data, cStats);
            threshold = DP_LOW_THRESHOLD;
        } else if (type == ThresholdBonusType.OP_ALL_LOW) {
            currValue = getTotalOP(data, cStats);
            threshold = OP_ALL_LOW_THRESHOLD;
        } else if (type == ThresholdBonusType.OP_ALL) {
            currValue = getTotalOP(data, cStats);
            threshold = OP_ALL_THRESHOLD;
        } else if (type == ThresholdBonusType.MILITARIZED_OP) {
            currValue = getMilitarizedOP(data, cStats);
            threshold = MILITARIZED_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.PHASE_DP) {
            currValue = getPhaseOP(data, cStats);
            threshold = PHASE_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.FIGHTER_BAYS_COMBAT) {
            currValue = getNumFighterBaysCombat(data);
            threshold = FIGHTER_BAYS_COMBAT_THRESHOLD;
        } else if (type == ThresholdBonusType.FRIGATE_DESTROYER_DP) {
            currValue = getFrigateDestroyerDP(data, cStats);
            threshold = FRIGATE_DESTROYER_DP_THRESHOLD;
        } else if (type == ThresholdBonusType.CRUISER_DP) {
            currValue = getCruiserDP(data, cStats);
            threshold = CRUISER_DP_THRESHOLD;
        } else if (type == ThresholdBonusType.CAPITAL_DP) {
            currValue = getCapitalDP(data, cStats);
            threshold = CAPITAL_DP_THRESHOLD;
        } else if (type == ThresholdBonusType.MISSILE_WEAPON_OP) {
            currValue = getMissileWeaponPoints(data);
            threshold = MISSILE_WEAPON_OP_THRESHOLD;
        } else if (type == ThresholdBonusType.PD_WEAPON_OP) {
            currValue = getPDWeaponPoints(data);
            threshold = PD_WEAPON_OP_THRESHOLD;
        } /*else if (type == ThresholdBonusType.AUTOMATED_POINTS) {
            currValue = getAutomatedPoints(data, cStats);
            threshold = AUTOMATED_POINTS_THRESHOLD;
        }*/

        bonus = getThresholdBasedRoundedBonus(maxBonus, currValue, threshold);
        data.getCacheClearedOnSync().put(key, bonus);
        return bonus;
    }

    public static float getThresholdBasedBonus(float maxBonus, float value, float threshold) {
        return maxBonus * threshold / Math.max(value, threshold);
    }

    public static float getThresholdBasedRoundedBonus(float maxBonus, float value, float threshold) {
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

    // -------------------------------------------------------------------------
    // Fleet & commander accessors
    // -------------------------------------------------------------------------

    public static boolean isInCampaign() {
        return Global.getCurrentState() == GameState.CAMPAIGN &&
               Global.getSector() != null &&
               Global.getSector().getPlayerFleet() != null;
    }

    public static FleetDataAPI getFleetData(MutableShipStatsAPI stats) {
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

    public static MutableCharacterStatsAPI getCommanderStats(MutableShipStatsAPI stats) {
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

    public static float getPoints(FleetMemberAPI member, MutableCharacterStatsAPI stats) {
        if (USE_RECOVERY_COST) {
            return member.getDeploymentPointsCost();
        }
        return member.getHullSpec().getOrdnancePoints(stats);
    }

    // -------------------------------------------------------------------------
    // Fleet totals
    // -------------------------------------------------------------------------

    public static float getTotalOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    public static float getTotalCombatOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    public static float getPhaseOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!curr.isPhaseShip()) continue;
            if (isCivilian(curr)) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    public static float getMilitarizedOP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float op = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (!isMilitarized(curr)) continue;
            op += getPoints(curr, stats);
        }
        return Math.round(op);
    }

    public static float getNumFighterBays(FleetDataAPI data) {
        if (data == null) return FIGHTER_BAYS_THRESHOLD;
        float bays = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            bays += getNumBaysIncludingModules(curr);
        }
        return bays;
    }

    public static float getNumBaysIncludingModules(FleetMemberAPI member) {
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

    /** Fighter bays count, excluding civilian ships. */
    public static float getNumFighterBaysCombat(FleetDataAPI data) {
        if (data == null) return 0f;
        float bays = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            bays += getNumBaysIncludingModules(curr);
        }
        return bays;
    }

    /** Total deployment point cost of non-civilian frigates and destroyers. */
    public static float getFrigateDestroyerDP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float dp = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            ShipAPI.HullSize size = curr.getHullSpec().getHullSize();
            if (size != ShipAPI.HullSize.FRIGATE && size != ShipAPI.HullSize.DESTROYER) continue;
            dp += getPoints(curr, stats);
        }
        return Math.round(dp);
    }

    /** Total deployment point cost of non-civilian cruisers. */
    public static float getCruiserDP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float dp = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            if (curr.getHullSpec().getHullSize() != ShipAPI.HullSize.CRUISER) continue;
            dp += getPoints(curr, stats);
        }
        return Math.round(dp);
    }

    /** Total deployment point cost of non-civilian capital ships. */
    public static float getCapitalDP(FleetDataAPI data, MutableCharacterStatsAPI stats) {
        float dp = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            if (curr.getHullSpec().getHullSize() != ShipAPI.HullSize.CAPITAL_SHIP) continue;
            dp += getPoints(curr, stats);
        }
        return Math.round(dp);
    }

    /**
     * Sum of missile weapon points across all non-civilian ships.
     * Each missile weapon slot counts as 1 (small), 2 (medium), or 3 (large) points.
     */
    public static float getMissileWeaponPoints(FleetDataAPI data) {
        float points = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            for (String slotId : curr.getVariant().getFittedWeaponSlots()) {
                WeaponSpecAPI spec = curr.getVariant().getWeaponSpec(slotId);
                if (spec == null) continue;
                if (spec.getType() != WeaponAPI.WeaponType.MISSILE) continue;
                points += getWeaponSizePoints(spec.getSize());
            }
        }
        return Math.round(points);
    }

    /**
     * Sum of point-defense weapon points across all non-civilian ships.
     * Each PD weapon slot counts as 1 (small), 2 (medium), or 3 (large) points.
     * Weapons with PD, PD_ONLY, or PD_ALSO AI hints are counted.
     */
    public static float getPDWeaponPoints(FleetDataAPI data) {
        float points = 0;
        for (FleetMemberAPI curr : data.getMembersListCopy()) {
            if (curr.isMothballed()) continue;
            if (isCivilian(curr)) continue;
            for (String slotId : curr.getVariant().getFittedWeaponSlots()) {
                WeaponSpecAPI spec = curr.getVariant().getWeaponSpec(slotId);
                if (spec == null) continue;
                if (!spec.getAIHints().contains(WeaponAPI.AIHints.PD) &&
                    !spec.getAIHints().contains(WeaponAPI.AIHints.PD_ONLY) &&
                    !spec.getAIHints().contains(WeaponAPI.AIHints.PD_ALSO)) continue;
                points += getWeaponSizePoints(spec.getSize());
            }
        }
        return Math.round(points);
    }

    /** Returns 2 for small, 4 for medium, 8 for large weapon slots. */
    public static int getWeaponSizePoints(WeaponAPI.WeaponSize size) {
        if (size == WeaponAPI.WeaponSize.MEDIUM) return 4;
        if (size == WeaponAPI.WeaponSize.LARGE) return 8;
        return 2;
    }

    public static float getAutomatedPoints(FleetDataAPI data, MutableCharacterStatsAPI stats) {
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

    // -------------------------------------------------------------------------
    // Ship classification helpers
    // -------------------------------------------------------------------------

    public static boolean isCivilian(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return isCivilian(stats.getFleetMember());
    }

    public static boolean isCivilian(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getVariant() != null &&
               ((stats.getVariant().hasHullMod(HullMods.CIVGRADE) &&
                 !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) ||
                (!stats.getVariant().hasHullMod(HullMods.CIVGRADE) &&
                  stats.getVariant().getHullSpec().getHints().contains(ShipTypeHints.CIVILIAN)));
    }

    public static boolean isMilitarized(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return isMilitarized(stats.getFleetMember());
    }

    public static boolean isMilitarized(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getVariant() != null &&
               stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS);
    }

    public static boolean hasFighterBays(MutableShipStatsAPI stats) {
        if (stats == null || stats.getFleetMember() == null) return false;
        return hasFighterBays(stats.getFleetMember());
    }

    public static boolean hasFighterBays(FleetMemberAPI member) {
        if (member == null) return false;
        MutableShipStatsAPI stats = member.getStats();
        return stats != null && stats.getNumFighterBays().getModifiedInt() > 0;
    }

    // -------------------------------------------------------------------------
    // Tooltip info methods
    // -------------------------------------------------------------------------

    public static void addFighterBayThresholdInfo(TooltipMakerAPI info, FleetDataAPI data) {
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

    public static void addOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        addOPThresholdInfo(info, data, cStats, OP_THRESHOLD);
    }

    public static void addOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
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

    public static void addOPThresholdAll(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
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

    public static void addPhaseOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
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

    public static void addMilitarizedOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
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

    public static void addFighterBaysCombatThresholdInfo(TooltipMakerAPI info, FleetDataAPI data) {
        addFighterBaysCombatThresholdInfo(info, data, FIGHTER_BAYS_COMBAT_THRESHOLD);
    }

    public static void addFighterBaysCombatThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (isInCampaign()) {
            int bays = Math.round(getNumFighterBaysCombat(data));
            String baysStr = bays == 1 ? "fighter bay" : "fighter bays";
            info.addPara(indent + "Maximum at %s or less used combat fighter bays in fleet, your fleet uses %s " + baysStr,
                    0f, tc, hc,
                    "" + (int) threshold,
                    "" + bays);
        }
    }

    public static void addFrigateDestroyerDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        addFrigateDestroyerDPThresholdInfo(info, data, cStats, FRIGATE_DESTROYER_DP_THRESHOLD);
    }

    public static void addFrigateDestroyerDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (isInCampaign()) {
            float dp = getFrigateDestroyerDP(data, cStats);
            info.addPara(indent + "Maximum at %s or less total frigate & destroyer " + RECOVERY_COST + ", your fleet's total is %s",
                    0f, tc, hc,
                    "" + (int) threshold,
                    "" + (int) Math.round(dp));
        } else {
            info.addPara(indent + "Maximum at %s or less total frigate & destroyer " + RECOVERY_COST + " for fleet",
                    0f, tc, hc,
                    "" + (int) threshold);
        }
    }

    public static void addCruiserDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        addCruiserDPThresholdInfo(info, data, cStats, CRUISER_DP_THRESHOLD);
    }

    public static void addCruiserDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        if (isInCampaign()) {
            float dp = getCruiserDP(data, cStats);
            info.addPara(indent + "Maximum at %s or less total cruiser " + RECOVERY_COST + ", your fleet's total is %s",
                    0f, tc, hc,
                    "" + (int) threshold,
                    "" + (int) Math.round(dp));
        } else {
            info.addPara(indent + "Maximum at %s or less total cruiser " + RECOVERY_COST + " for fleet",
                    0f, tc, hc,
                    "" + (int) threshold);
        }
    }

    public static void addCapitalDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats) {
        addCapitalDPThresholdInfo(info, data, cStats, CAPITAL_DP_THRESHOLD);
    }

    public static void addCapitalDPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, MutableCharacterStatsAPI cStats, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();
        String indent = BaseIntelPlugin.BULLET;
        float dp = getCapitalDP(data, cStats);
        info.addPara("   - Maximum at %s or less total capital ship " + RECOVERY_COST + ", your fleet's total is %s",
                0f, tc, hc,
                "" + (int) threshold,
                "" + (int) Math.round(dp));
    }

    public static void addMissileWeaponOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data) {
        addMissileWeaponOPThresholdInfo(info, data, MISSILE_WEAPON_OP_THRESHOLD);
    }

    public static void addMissileWeaponOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();

        float pts = getMissileWeaponPoints(data);
        info.addPara("   - Each missiles weapon counts as 2/4/8 points, based on weapon size", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "2/4/8");
        info.addPara("   - Maximum at %s or less missile weapon points in fleet, your fleet's total is %s",
                0f, tc, hc,
                "" + (int) threshold,
                "" + (int) Math.round(pts));
    }

    public static void addPDWeaponOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data) {
        addPDWeaponOPThresholdInfo(info, data, PD_WEAPON_OP_THRESHOLD);
    }

    public static void addPDWeaponOPThresholdInfo(TooltipMakerAPI info, FleetDataAPI data, float threshold) {
        Color tc = Misc.getTextColor();
        Color hc = Misc.getHighlightColor();

        float pts = getMissileWeaponPoints(data);
        info.addPara("   - Each point-defense weapon counts as 2/4/8 points, based on weapon size", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "2/4/8");
        info.addPara("   - Maximum at %s or less point-defemse weapon points in fleet, your fleet's total is %s",
                0f, tc, hc,
                "" + (int) threshold,
                "" + (int) Math.round(pts));
    }
}
