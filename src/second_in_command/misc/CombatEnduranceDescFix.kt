package second_in_command.misc

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.characters.SkillSpecAPI
import com.fs.starfarer.api.impl.campaign.skills.CombatEndurance
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

class CombatEnduranceDescFix : CombatEndurance.Level4() {

    override fun createCustomDescription(stats: MutableCharacterStatsAPI?,  skill: SkillSpecAPI?, info: TooltipMakerAPI?, width: Float) {

        initElite(stats, skill)

        tc = Misc.getTextColor()
        hc = Misc.getHighlightColor()

        info!!.addPara("When below %s hull, repair %s per second; maximum total repair is " + "the higher of %s points or %s of maximum hull", 0f, hc, hc,
            "" + Math.round(CombatEndurance.MAX_REGEN_LEVEL * 100f) + "%",  //"" + (int)Math.round(REGEN_RATE * 100f) + "%",
            "" + Misc.getRoundedValueMaxOneAfterDecimal(CombatEndurance.REGEN_RATE * 100f) + "%",
            "" + Math.round(CombatEndurance.TOTAL_REGEN_MAX_POINTS) + "",
            "" + Math.round(CombatEndurance.TOTAL_REGEN_MAX_HULL_FRACTION * 100f) + "%")
    }

}