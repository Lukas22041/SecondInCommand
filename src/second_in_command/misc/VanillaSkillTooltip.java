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

            boolean isEliteGroup = i > 0;
            boolean skillActive = level >= 1f;
            boolean eliteActive = level >= 2f;
            // Only dim when the skill is acquired but not yet elite; leave full colour when inactive
            boolean dimElite = isEliteGroup && skillActive && !eliteActive;

            if (isEliteGroup && !eliteLabel) {
                eliteLabel = true;
                prev = tooltip.addSpacer(10f);
                Color base = Misc.getStoryOptionColor();
                Color eliteLabelColor = dimElite
                        ? new Color(base.getRed(), base.getGreen(), base.getBlue(), (int)(base.getAlpha() * 0.60f))
                        : base;
                tooltip.addPara("Elite", eliteLabelColor, 0).getPosition().belowLeft(prev, 0);
                prev = tooltip.addSpacer(5f);
            }

            for (Object effect : effectGroup) {
                if (printed.contains(effect.getClass())) continue;
                printed.add(effect.getClass());

                if (effect instanceof CustomSkillDescription) {
                    CustomSkillDescription desc = ((CustomSkillDescription) effect);
                    if (desc.hasCustomDescription()) {
                        desc.createCustomDescription(person.getStats(), skill, tooltip, getTooltipWidth(null));

                        if (dimElite) {
                            UIComponentAPI comp = tooltip.getPrev();
                            if (comp instanceof LabelAPI) {
                                Color c = Misc.getTextColor();
                                ((LabelAPI) comp).setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * 0.60f)));
                            }
                        }

                        prev = tooltip.getPrev();
                        continue;
                    }
                }

                if (effect instanceof LevelBasedEffect) {
                    LevelBasedEffect levelEff = ((LevelBasedEffect) effect);
                    float effectLevel = dimElite ? 0f : level;
                    Color base = Misc.getHighlightColor();
                    Color textColor = dimElite
                            ? new Color(base.getRed(), base.getGreen(), base.getBlue(), (int)(base.getAlpha() * 0.60f))
                            : base;
                    tooltip.addPara(levelEff.getEffectDescription(effectLevel), textColor, 0).getPosition().belowLeft(prev, 0);
                } else if (effect instanceof DescriptionSkillEffect) {
                    DescriptionSkillEffect desc = ((DescriptionSkillEffect) effect);
                    Color base = desc.getTextColor();
                    Color textColor = dimElite
                            ? new Color(base.getRed(), base.getGreen(), base.getBlue(), (int)(base.getAlpha() * 0.60f))
                            : base;
                    LabelAPI label = tooltip.addPara(desc.getString(), textColor, 0);
                    label.setHighlight(desc.getHighlights());
                    if (dimElite) {
                        Color[] highlightColors = desc.getHighlightColors();
                        Color[] dimColors = new Color[highlightColors.length];
                        for (int j = 0; j < dimColors.length; j++) {
                            Color hc = highlightColors[j];
                            dimColors[j] = new Color(hc.getRed(), hc.getGreen(), hc.getBlue(), (int)(hc.getAlpha() * 0.60f));
                        }
                        label.setHighlightColors(dimColors);
                    } else {
                        label.setHighlightColors(desc.getHighlightColors());
                    }
                    label.getPosition().belowLeft(prev, 0);
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
            tooltip.addPara("Hull mod: " + hullmodSpec.getDisplayName() + " - " + ReflectionUtils.invoke("getShortDesc", hullmodSpec, new Object[0], null, null) , 0f,
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

        if (level == 1f) {
            tooltip.addSpacer(10f);
            tooltip.addPara("Requires a story point to make elite", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
        }


        tooltip.addSpacer(3f);
    }

    public static VanillaSkillTooltip addToTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, int requiredSkillPoints) {
        VanillaSkillTooltip element = new VanillaSkillTooltip(tooltip, person, skillSpec, requiredSkillPoints);
        tooltip.addTooltipToPrevious(element, TooltipMakerAPI.TooltipLocation.BELOW, false);
        return element;
    }
}
