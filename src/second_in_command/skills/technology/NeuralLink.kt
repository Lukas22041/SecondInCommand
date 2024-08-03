package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class NeuralLink : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "ships with the Neural interface hullmod"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        val control = Global.getSettings().getControlStringForEnumName(NeuralLinkScript.TRANSFER_CONTROL)
        val desc = Global.getSettings().getControlDescriptionForEnumName(NeuralLinkScript.TRANSFER_CONTROL)

        tooltip.addPara("Allows two ships to benefit from your skills at the same time and enables rapid switching between them", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The destination ship must not be under the command of an officer or an AI core", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "must not be")

        tooltip.addSpacer(10f)

        tooltip.addPara("Hull mod: Neural Interface - allows rapid switching between ships", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Neural Interface")
        tooltip.addPara("Hull mod: Neural Integrator - specialised Neural Interface for automated ships", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Neural Integrator")

        tooltip.addSpacer(15f)

        tooltip.addPara("*Use the \"$desc\" control [$control] to switch between ships", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "$control")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {



    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }



    override fun onActivation(data: SCData) {
        var faction = Global.getSector().playerFaction
        if (!faction.knownHullMods.contains(HullMods.NEURAL_INTEGRATOR)) {
            faction.addKnownHullMod(HullMods.NEURAL_INTEGRATOR)
        }
        if (!faction.knownHullMods.contains(HullMods.NEURAL_INTERFACE)) {
            faction.addKnownHullMod(HullMods.NEURAL_INTERFACE)
        }

        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.HAS_NEURAL_LINK).modifyFlat("sc_neural_link", 1f)
    }

    //In case vanilla neural link deactivates it
    override fun advance(data: SCData, amount: Float) {
        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.HAS_NEURAL_LINK).modifyFlat("sc_neural_link", 1f)
    }

    override fun onDeactivation(data: SCData) {
        Global.getSector().characterData.person.stats.dynamic.getMod(Stats.HAS_NEURAL_LINK).unmodify("sc_neural_link")
    }

}