package second_in_command.skills.scavenging

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.specs.SCBaseSkillPlugin

class MakeshiftMeasures : SCBaseSkillPlugin() {

    companion object {
        var SUSTAINED_COST_PER_DAY = 10f
        var GO_DARK_COST_PER_DAY = 10f

        var IS_ACTIVE_KEY = "\$sc_makeshift_is_active"
    }


    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("While holding \"CTRL\" the \"Sustained Burn\" ability gets temporarily upgraded", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The fleets maximum burn speed is increased by 4", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "4")
        tooltip.addPara("   - The fleets maneuverability is doubled", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "doubled")
        tooltip.addPara("   - The fleet uses up ${SUSTAINED_COST_PER_DAY.toInt()}%% of Scrap per day", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${SUSTAINED_COST_PER_DAY.toInt()}%")
        tooltip.addPara("   - The ability does not work if you do not have any Scrap", 0f, Misc.getTextColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("While holding \"CTRL\" the \"Go Dark\" ability gets temporarily upgraded", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The fleets sensor profile is reduced by 30%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "30%")
        tooltip.addPara("   - The fleets burn level at which it is considered \"slow-moving\" is increased by 3*", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "3")
        tooltip.addPara("   - The fleet uses up ${GO_DARK_COST_PER_DAY.toInt()}%% of Scrap per day", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${GO_DARK_COST_PER_DAY.toInt()}%")
        tooltip.addPara("   - The ability does not work if you do not have any Scrap", 0f, Misc.getTextColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)
        tooltip.addPara("*A slow moving fleet is harder to detect in some types of terrain, and can avoid some hazards. Some abilities also make the fleet " +
                "move slowly when activated. A fleet is considered slow-moving at a burn level of half of its slowest ship.", 0f, Misc.getGrayColor(), Misc.getHighlightColor())
    }


    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

        var fleet = data.fleet
        var sustainedBurn = fleet.getAbility(Abilities.SUSTAINED_BURN)
        var goDark = fleet.getAbility(Abilities.GO_DARK)
        var scrapManager = data.scrapManager

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && sustainedBurn != null && sustainedBurn.isActive && scrapManager.getCurrentScrap() > 0f) {
            fleet.stats.fleetwideMaxBurnMod.modifyFlat("sc_makeshift_measures", 3f, "Makeshift Measures Skill")
            fleet.stats.accelerationMult.modifyMult("sc_makeshift_measures", 2f, "Makeshift Measures Skill")

            var days = Global.getSector().clock.convertToDays(amount)
            scrapManager.adjustScrap(-SUSTAINED_COST_PER_DAY * days)
            scrapManager.setScrapConsumptionThisFrame(SUSTAINED_COST_PER_DAY)

            fleet.memoryWithoutUpdate.set(IS_ACTIVE_KEY, true, 0.1f)
        } else {
            fleet.stats.fleetwideMaxBurnMod.unmodify("sc_makeshift_measures")
            fleet.stats.accelerationMult.unmodify("sc_makeshift_measures")
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && goDark != null && goDark.isActive && scrapManager.getCurrentScrap() > 0f) {

            data.fleet.stats.detectedRangeMod.modifyMult("sc_makeshift_measures", 0.70f, "Makeshift Measures Skill")
            data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).modifyFlat("sc_makeshift_measures", 3f, "Makeshift Measures Skill")

            var days = Global.getSector().clock.convertToDays(amount)
            scrapManager.adjustScrap(-GO_DARK_COST_PER_DAY * days)
            scrapManager.setScrapConsumptionThisFrame(GO_DARK_COST_PER_DAY)

            fleet.memoryWithoutUpdate.set(IS_ACTIVE_KEY, true, 0.1f)
        } else {
            data.fleet.stats.detectedRangeMod.unmodify("sc_makeshift_measures")
            data.fleet.stats.dynamic.getMod(Stats.MOVE_SLOW_SPEED_BONUS_MOD).unmodify("sc_makeshift_measures")
        }



    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}