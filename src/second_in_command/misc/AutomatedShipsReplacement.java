package second_in_command.misc;

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.skills.AutomatedShips;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCUtils;

public class AutomatedShipsReplacement extends AutomatedShips.Level2 {

    @Override
    public void apply(MutableCharacterStatsAPI stats, String id, float level) {

        if (second_in_command.skills.automated.AutomatedShips.Companion.isAnyAutoSkillActive()) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        }

        if (!SCUtils.getSCData().isModEnabled()) {
            super.unapply(stats, id);
        }
    }


    @Override
    public void unapply(MutableCharacterStatsAPI stats, String id) {

        if (second_in_command.skills.automated.AutomatedShips.Companion.isAnyAutoSkillActive()) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        }

        if (!SCUtils.getSCData().isModEnabled()) {
            super.unapply(stats, id);
        }
    }
}
