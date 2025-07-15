package second_in_command.skills.starfaring

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.ui.trade.CargoItemStack
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class RecoveryEfforts : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All of your ships are almost always recoverable if lost in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+40%% repair rate outside of combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+25%% of hull and armor damage taken repaired after combat ends, at no cost", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.dynamic.getMod(Stats.INSTA_REPAIR_FRACTION).modifyFlat(id, 0.25f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_recovery_efforts", 2f)
        data.commander.stats.repairRateMult.modifyPercent("sc_recovery_efforts", 40f)

    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat("sc_recovery_efforts", 2f)
        data.commander.stats.repairRateMult.modifyPercent("sc_recovery_efforts", 40f)

    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.SHIP_RECOVERY_MOD).unmodify("sc_recovery_efforts")
        data.commander.stats.repairRateMult.unmodify("sc_recovery_efforts")
    }

}