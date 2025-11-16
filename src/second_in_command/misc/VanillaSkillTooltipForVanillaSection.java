package second_in_command.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaUI.elements.LunaElement;

import java.sql.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//From:
//https://github.com/scardwell15/takenoprisoners/blob/main/src/takenoprisoners/ui/SkillTooltip.java
public class VanillaSkillTooltipForVanillaSection extends BaseTooltipCreator {
    private final TooltipMakerAPI tooltip;
    private final PersonAPI person;
    private final SkillSpecAPI skill;
    private final List<List<Object>> effects;
    public float level = 0f;

    public VanillaSkillTooltipForVanillaSection(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec) {
        this.tooltip = tooltip;
        this.person = person;
        this.skill = skillSpec;
        this.effects = VanillaSkillsUtil.getLevelEffects(skillSpec.getId());
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 800f;
    }

    public String getAffectsString(SkillSpecAPI skill) {
        if (skill.getScopeStr() != null)
        {
            return skill.getScopeStr();
        }
        if (skill.getScope() == null) return null;
        return switch (skill.getScope()) {
            case PILOTED_SHIP -> "piloted ship";
            case ALL_SHIPS -> "all ships";
            case ALL_COMBAT_SHIPS -> "all combat ships";
            case ALL_CARRIERS -> "all carriers";
            case ALL_FIGHTERS -> "all fighters";
            case SHIP_FIGHTERS -> "fighters";
            case GOVERNED_OUTPOST -> "governed colonies";
            case ALL_OUTPOSTS -> "all colonies";
            case FLEET -> "fleet";
            case CUSTOM -> "custom";
            case NONE -> "none";
        };
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

        //tooltip.setParaFont(Fonts.ORBITRON_12);
        LabelAPI description = tooltip.addPara(skill.getDescription(), 0f);
        if (!skill.getAuthor().isBlank()) {
            LabelAPI author = tooltip.addPara("- " + skill.getAuthor(), 0f, Misc.getGrayColor(), Misc.getGrayColor());
            LunaElement anchor = new LunaElement(tooltip, 0f, 0f);
            anchor.getPosition().belowLeft((UIComponentAPI) description, 10f);
            author.getPosition().rightOfMid(anchor.getElementPanel(), getTooltipWidth(null)-author.computeTextWidth(author.getText())-30f);
        }
       // tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addSpacer(10f);

        String affects = getAffectsString(skill);
        if (affects != null) {
            tooltip.addPara("Affects: " + affects, 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), affects);
            tooltip.addSpacer(10f);
        }

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
                    //throw new RuntimeException("Unexpected effect class " + effect + " " + effect.getClass());
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

        tooltip.addSpacer(2f);
    }

    public static VanillaSkillTooltipForVanillaSection addToTooltip(TooltipMakerAPI tooltip, PersonAPI person, SkillSpecAPI skillSpec, int requiredSkillPoints) {
        VanillaSkillTooltipForVanillaSection element = new VanillaSkillTooltipForVanillaSection(tooltip, person, skillSpec);
        tooltip.addTooltipToPrevious(element, TooltipMakerAPI.TooltipLocation.BELOW, false);
        return element;
    }
}
