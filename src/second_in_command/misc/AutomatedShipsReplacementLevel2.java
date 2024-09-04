package second_in_command.misc;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.skills.AutomatedShips;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCUtils;
import second_in_command.scripts.AutomatedShipsManager;


public class AutomatedShipsReplacementLevel2 extends AutomatedShips.Level2 {

    @Override
    public void apply(MutableCharacterStatsAPI stats, String id, float level) {
        if (Global.getCurrentState() == GameState.TITLE) return;
        if (Global.getSector().getPlayerFleet() == null) return;
        if (Global.getSector().getPlayerPerson() == null) return;

        if (AutomatedShipsManager.get().canAcquireAutoShips()) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        }

        /*if (!SCUtils.getPlayerData().isModEnabled()) {
            super.apply(stats, id, level);
        }*/
    }


    @Override
    public void unapply(MutableCharacterStatsAPI stats, String id) {

        if (Global.getCurrentState() == GameState.TITLE) return;
        if (Global.getSector().getPlayerFleet() == null) return;
        if (Global.getSector().getPlayerPerson() == null) return;

        if (AutomatedShipsManager.get().canAcquireAutoShips()) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE);
        }

        /*if (!SCUtils.getPlayerData().isModEnabled()) {
            super.unapply(stats, id);
        }*/
    }
}
