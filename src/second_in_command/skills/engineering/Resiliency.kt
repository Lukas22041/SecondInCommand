package second_in_command.skills.engineering

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.skills.improvisation.Preservation
import second_in_command.specs.SCBaseSkillPlugin

class Resiliency : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Reduces most negative effects of d-mods by 25%%*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+30 seconds of peak performance time", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("*This effect stacks multiplicatively with others of the same kind", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "multiplicatively")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.peakCRDuration.modifyFlat(id, 30f)
        stats.dynamic.getStat(Stats.DMOD_EFFECT_MULT).modifyMult(id, 0.75f)
        Preservation.reapplyDmods(variant, hullSize, stats)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}