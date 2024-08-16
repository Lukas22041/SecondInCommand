package second_in_command.skills.improvisation

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class DefensiveConfiguration : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All ships have slightly improved autofire accuracy", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+100 point defense weapon range", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+33%% weapon turn rate", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats.autofireAimAccuracy.modifyFlat(id, 0.2f)
        stats.nonBeamPDWeaponRangeBonus.modifyFlat(id, 100f)
        stats.beamPDWeaponRangeBonus.modifyFlat(id, 100f)
        stats.weaponTurnRateBonus.modifyPercent(id, 33f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amunt: Float?) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }


}