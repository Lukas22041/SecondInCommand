package second_in_command.misc;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.skills.AutomatedShips;
import second_in_command.SCUtils;

public class AutomatedShipsReplacementLevel1 extends AutomatedShips.Level1 {

    @Override
    public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
       /* if (!SCUtils.getPlayerData().isModEnabled()) {
            super.apply(stats, hullSize, id, level);
        }*/
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
        /*if (!SCUtils.getPlayerData().isModEnabled()) {
            super.unapply(stats, hullSize, id);
        }*/
    }
}
