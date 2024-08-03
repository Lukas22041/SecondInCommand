package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FluxRegulation : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+5 maximum flux capacitors and vents for all loadouts", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this officer is unassigned, capacitors and vents over the limit are removed", 0f, Misc.getTextColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("+10%% flux dissipation for combat ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+10%% flux capacity for combat ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData,
                                                stats: MutableShipStatsAPI?,
                                                variant: ShipVariantAPI,
                                                hullSize: ShipAPI.HullSize?,
                                                id: String?) {

        if (!BaseSkillEffectDescription.isCivilian(stats)) {
            stats!!.fluxDissipation.modifyPercent(id, 10f)
            stats!!.fluxCapacity.modifyPercent(id, 10f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun advance(data: SCData, amount: Float) {
        Global.getSector().characterData.person.stats.maxVentsBonus.modifyFlat("sc_flux_regulation", 5f)
        Global.getSector().characterData.person.stats.maxCapacitorsBonus.modifyFlat("sc_flux_regulation", 5f)
    }

    override fun onDeactivation(data: SCData) {
        Global.getSector().characterData.person.stats.maxVentsBonus.unmodify("sc_flux_regulation")
        Global.getSector().characterData.person.stats.maxCapacitorsBonus.unmodify("sc_flux_regulation")

    }

}