package second_in_command.skills.technology

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FluxRegulation : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        /*tooltip.addPara("+5 maximum flux capacitors and vents for all loadouts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this officer is unassigned, capacitors and vents over the limit are removed", 0f, Misc.getTextColor(), Misc.getHighlightColor())*/


        /*tooltip.addPara("Flux capacity from capacitors is increased by 20", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Flux dissipation from vents is increased by 2", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())*/


        tooltip.addPara("+10%% flux dissipation", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% flux capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("Capacitors and Vents provide more flux capacity and dissipation", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   +10 extra flux capacity per capacitor on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+10")
        tooltip.addPara("   +1 extra flux dissipation per vent on the ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+1")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var vents = stats!!.variant.numFluxVents
        var caps = stats!!.variant.numFluxCapacitors
        var fluxIncrease = 1f * vents
        var capsIncrease = 10f * caps

        stats.fluxDissipation.modifyFlat(id, fluxIncrease)
        stats.fluxCapacity.modifyFlat(id, capsIncrease)

        stats!!.fluxDissipation.modifyPercent(id, 10f)
        stats!!.fluxCapacity.modifyPercent(id, 10f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData, amount: Float) {
       /* data.commander.stats.maxVentsBonus.modifyFlat("sc_flux_regulation", 5f)
        data.commander.stats.maxCapacitorsBonus.modifyFlat("sc_flux_regulation", 5f)*/
    }

    override fun onDeactivation(data: SCData) {
        /*data.commander.stats.maxVentsBonus.unmodify("sc_flux_regulation")
        data.commander.stats.maxCapacitorsBonus.unmodify("sc_flux_regulation")*/

    }

}