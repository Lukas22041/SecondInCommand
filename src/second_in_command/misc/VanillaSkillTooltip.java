package second_in_command.misc;

import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//From:
//https://github.com/scardwell15/takenoprisoners/blob/main/src/takenoprisoners/ui/SkillTooltip.java
public class VanillaSkillTooltip extends BaseTooltipCreator {
    private final TooltipMakerAPI tooltip;
    private final PersonAPI person;
    private final SkillSpecAPI skill;
    private final List<List<Object>> effects;
    public float level = 0f;
    public int requiredSkillPoints;
    public boolean sectionMeetsRequirements = true;

    public VanillaSkillTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, int requiredSkillPoints) {
        this.tooltip = tooltip;
        this.person = person;
        this.skill = skillSpec;
        this.requiredSkillPoints = requiredSkillPoints;
        this.effects = VanillaSkillsUtil.getLevelEffects(skillSpec.getId());
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 800f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addTitle(skill.getName(), skill.getGoverningAptitudeColor());
        tooltip.addSpacer(10f);

        tooltip.setCodexEntryId(CodexDataV2.getSkillEntryId(skill.getId()));

       /* if (skill.getScopeStr() != null) {
            tooltip.addPara(skill.getScopeStr(), 0);
        }

        if (skill.getScopeStr2() != null) {
            tooltip.addPara(skill.getScopeStr2(), 0);
        }*/

        String extra = "";
        if (skill.getId().equals("point_defense")) {
            extra += " and fighters from the piloted ship";
        }

        tooltip.addPara("Affects: piloted ship" + extra, 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), "piloted ship", "fighters from the piloted ship");
        tooltip.addSpacer(10f);



        Set<Class> printed = new HashSet<>();
        UIComponentAPI prev = tooltip.getPrev();
        boolean eliteLabel = false;
        for (int i = 0; i < effects.size(); i++) {
            List<Object> effectGroup = effects.get(i);

            if (i > 0 && !eliteLabel) {
             /*   eliteLabel = true;
                tooltip.addTitle("Elite", Misc.getStoryOptionColor()).getPosition().belowLeft(prev, 3);
                prev = tooltip.getPrev();*/
                prev = tooltip.addSpacer(10f);
            }

            for (Object effect : effectGroup) {
                if (printed.contains(effect.getClass())) continue;
                printed.add(effect.getClass());

                if (effect instanceof CustomSkillDescription) {
                    CustomSkillDescription desc = ((CustomSkillDescription) effect);
                    if (desc.hasCustomDescription()) {
                        desc.createCustomDescription(person.getStats(), skill, tooltip, getTooltipWidth(null));

                        prev = tooltip.getPrev();
                        continue;
                    }
                }

                if (effect instanceof LevelBasedEffect) {
                    LevelBasedEffect levelEff = ((LevelBasedEffect) effect);
                    tooltip.addPara(levelEff.getEffectDescription(level), Misc.getHighlightColor(), 0).getPosition().belowLeft(prev, 1);
                } else if (effect instanceof DescriptionSkillEffect) {
                    DescriptionSkillEffect desc = ((DescriptionSkillEffect) effect);
                    LabelAPI label = tooltip.addPara(desc.getString(), desc.getTextColor(), 0);
                    label.setHighlight(desc.getHighlights());
                    label.setHighlightColors(desc.getHighlightColors());
                    label.getPosition().belowLeft(prev, 1);
                } else {
                    throw new RuntimeException("Unexpected effect class " + effect + " " + effect.getClass());
                }

                prev = tooltip.getPrev();
            }
        }

        List<HullModSpecAPI> hullmodSpecs = VanillaSkillsUtil.getUnlockedHullmods(skill.getId());

        if (!hullmodSpecs.isEmpty()) {
            tooltip.addSpacer(10f);
        }

        for (HullModSpecAPI hullmodSpec: hullmodSpecs) {
            tooltip.addPara("Hull mod: " + hullmodSpec.getDisplayName() + " - " + ReflectionUtils.invoke("getShortDesc", hullmodSpec, new Array[]{}, null, null) , 0f,
                    Misc.getTextColor(), Misc.getHighlightColor(), hullmodSpec.getDisplayName());
        }

        if (skill.getId().equals("polarized_armor")) {
            tooltip.addSpacer(10f);
            tooltip.addPara("*Ships without a shield or a phase cloak are treated as always having 50%% hard flux", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "50%");
        }

        if (requiredSkillPoints >= 2) {
            tooltip.addSpacer(10f);
            tooltip.addPara("Requires " + requiredSkillPoints + " skill points.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
        }

        tooltip.addSpacer(2f);
    }

    public static VanillaSkillTooltip addToTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, int requiredSkillPoints) {
        VanillaSkillTooltip element = new VanillaSkillTooltip(tooltip, person, skillSpec, requiredSkillPoints);
        tooltip.addTooltipToPrevious(element, TooltipMakerAPI.TooltipLocation.BELOW, false);
        return element;
    }
}
