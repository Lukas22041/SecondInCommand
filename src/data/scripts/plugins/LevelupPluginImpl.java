package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.plugins.LevelupPlugin;
import second_in_command.misc.SCSettings;

public class LevelupPluginImpl implements LevelupPlugin {

    /**
     * Only used if max level is increased beyond 15 via settings.json
     */
    public static float EXPONENT_BEYOND_MAX_SPECIFIED_LEVEL = 1.1f;

    /**
     * Max level XP times this is how much XP it takes to gain storyPointsPerLevel story points once at max level.
     */
    public static float XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT = 2f;
    public static int LEVEL_FOR_BASE_XP_FOR_MAXED_STORY_POINT_GAIN = 15;

    public static long [] XP_PER_LEVEL = new long [] {
            0,		// level 1
            50000,
            70000,
            90000,
            100000,  // level 5, ramp up after

            300000,
            500000,
            700000,
            900000,
            1000000, // level 10, ramp up after

            1200000,
            1400000,
            1600000,
            1800000,
            2000000, // level 15

            2200000, //LV16, Only active if config increases the max level
            2400000,
            2600000,
            2800000,
            3000000, //LV20

            3200000,
            3400000,
            3600000,
            3800000,
            4000000, //LV25
    };

    public static long [] TOTAL_XP_PER_LEVEL = new long [XP_PER_LEVEL.length];

    static {
        long total = 0;
        for (int i = 0; i < XP_PER_LEVEL.length; i++) {
            total += XP_PER_LEVEL[i];
            TOTAL_XP_PER_LEVEL[i] = total;
        }
    }





    public int getPointsAtLevel(int level) {

        if (level % 2 != 0) {
            return 1;
        }
        return 0;

    }

    public int getMaxLevel() {
        //return (int) Global.getSettings().getFloat("playerMaxLevel");
        return SCSettings.getPlayerMaxLevel();
    }

    public int getStoryPointsPerLevel() {
        return (int) Global.getSettings().getFloat("storyPointsPerLevel");
    }

    public int getBonusXPUseMultAtMaxLevel() {
        return (int) Global.getSettings().getFloat("bonusXPUseMultAtMaxLevel");
    }

    public long getXPForLevel(int level) {
        if (level <= 1) return 0;

        if (level - 1 < TOTAL_XP_PER_LEVEL.length) {
            return TOTAL_XP_PER_LEVEL[level - 1];
        }

        int max = getMaxLevel();
        int maxSpecified = TOTAL_XP_PER_LEVEL.length;
        long curr = TOTAL_XP_PER_LEVEL[maxSpecified - 1];
        long last = XP_PER_LEVEL[maxSpecified - 1];
        for (int i = maxSpecified; i < level && i < max; i++) {
            last *= EXPONENT_BEYOND_MAX_SPECIFIED_LEVEL;
            curr += last;
        }

        if (level >= max + 1) {
            //last *= XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT;
            last = (long) (XP_PER_LEVEL[LEVEL_FOR_BASE_XP_FOR_MAXED_STORY_POINT_GAIN - 1] *
                    XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT);
            curr += last;
        }

        return curr;
    }


}
