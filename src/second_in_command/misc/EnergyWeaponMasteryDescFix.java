package second_in_command.misc;

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.impl.campaign.skills.EnergyWeaponMastery;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class EnergyWeaponMasteryDescFix  extends EnergyWeaponMastery.Level1 {

    @Override
    public void createCustomDescription(MutableCharacterStatsAPI stats, SkillSpecAPI skill, TooltipMakerAPI info, float width) {

        init(stats, skill);

        tc = Misc.getTextColor();
        hc = Misc.getHighlightColor();



        info.addPara("Energy weapons deal up to %s damage at close range, based on the firing ship's flux level",
                0f, hc, hc,
                "+" + (int) EnergyWeaponMastery.ENERGY_DAMAGE_PERCENT + "%"
        );


        info.addPara(indent + "Full bonus damage at %s range and below, " +
                        "no bonus damage at %s range and above",
                0f, tc, hc,
                "" + (int) EnergyWeaponMastery.MIN_RANGE,
                "" + (int) EnergyWeaponMastery.MAX_RANGE
        );
    }
}
