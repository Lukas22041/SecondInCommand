package second_in_command.skills.scavenging

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.specs.SCBaseSkillPlugin

class Pinpointing : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+20%% to the chance that opponents drop their weapons after being destroyed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("Affects: all ships in the fleet" , 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), "all ships in the fleet")

        tooltip.addSpacer(10f)

        tooltip.addPara("+30%% damage dealt to weapons in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% energy and ballistic projectile speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {
        stats.damageToTargetWeaponsMult.modifyPercent(id, 30f)
        stats.energyProjectileSpeedMult.modifyPercent(id, 10f)
        stats.ballisticProjectileSpeedMult.modifyPercent(id, 10f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_pinpointing", 0.20f)
    }

    override fun advance(data: SCData, amount: Float) {
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_pinpointing", 0.20f)
    }

    override fun onActivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).modifyFlat("sc_pinpointing", 0.20f)
    }

    override fun onDeactivation(data: SCData) {
        data.fleet.stats.dynamic.getMod(Stats.ENEMY_WEAPON_RECOVERY_MOD).unmodify("sc_pinpointing")
    }

}