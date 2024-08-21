package second_in_command.misc;

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.skills.AutomatedShips;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import second_in_command.skills.misc.SCBaseVanillaShipSkill;

public class DerelictOperationsReplacementLevel1 extends SCBaseVanillaShipSkill {


    @NotNull
    @Override
    public ScopeDescription getScopeDescription() {
        return null;
    }

    @Override
    public void createCustomDescription(@Nullable MutableCharacterStatsAPI stats, @Nullable SkillSpecAPI skill, @Nullable TooltipMakerAPI info, float width) {

    }

    @Override
    public void apply(@Nullable MutableShipStatsAPI stats, @Nullable ShipAPI.HullSize hullSize, @Nullable String id, float level) {
        String test = "";
    }

    @Override
    public void unapply(@Nullable MutableShipStatsAPI stats, @Nullable ShipAPI.HullSize hullSize, @Nullable String id) {
        String test = "";
    }
}
