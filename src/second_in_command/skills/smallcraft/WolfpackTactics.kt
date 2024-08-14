package second_in_command.skills.smallcraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class WolfpackTactics : SCBaseSkillPlugin() {

    var DAMAGE_TO_LARGER_BONUS = 20f
    var DAMAGE_TO_LARGER_BONUS_DEST = 10f
    var PEAK_TIME_BONUS = 50f
    var PEAK_TIME_BONUS_DEST = 25f

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and Destroyers deal increased damage against larger targets", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 20%% increased damage against larger hullsizes for frigates", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - 10%% increased damage against larger hullsizes for destroyers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

        tooltip.addSpacer(10f)

        tooltip.addPara("Increased peak operating time for frigates and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 50%% increased operating time for frigates", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "50%")
        tooltip.addPara("   - 25%% increased operating time for destroyers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "25%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (hullSize == ShipAPI.HullSize.FRIGATE) {
            stats!!.damageToDestroyers.modifyPercent(id,DAMAGE_TO_LARGER_BONUS)
            stats!!.damageToCruisers.modifyPercent(id, DAMAGE_TO_LARGER_BONUS)
            stats!!.damageToCapital.modifyPercent(id, DAMAGE_TO_LARGER_BONUS)

            stats!!.peakCRDuration.modifyPercent(id, PEAK_TIME_BONUS)
        }
        if (hullSize == ShipAPI.HullSize.DESTROYER) {
            stats!!.damageToCruisers.modifyPercent(id, DAMAGE_TO_LARGER_BONUS_DEST)
            stats!!.damageToCapital.modifyPercent(id, DAMAGE_TO_LARGER_BONUS_DEST)

            stats!!.peakCRDuration.modifyPercent(id, PEAK_TIME_BONUS_DEST)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}