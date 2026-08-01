package second_in_command.skills.engineering

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.StatBonus
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.patches.SCCargoItemStackPatch
import second_in_command.specs.SCBaseSkillPlugin

class CompactStorage : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+30%% cargo capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Weapons in your fleets cargo require only 33%% of their original storage space", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.cargoMod.modifyPercent(id, 30f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {
        if (data.isPlayer) {
            val bonus = data.commander.stats.dynamic.getMod(SCCargoItemStackPatch.WEAPON_STORAGE_MOD_KEY)
            bonus.modifyMult("sc_compact_storage", 0.33f)
            data.fleet.cargo.updateSpaceUsed();
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.isPlayer) {
            val bonus = data.commander.stats.dynamic.getMod(SCCargoItemStackPatch.WEAPON_STORAGE_MOD_KEY).unmodify("sc_compact_storage")
            data.fleet.cargo.updateSpaceUsed();
        }

    }

}