package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class OfficerManagement : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("+2 to maximum number of officers you're able to command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this executive officer is unassigned, any officer over the limit will also be unassigned", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")


    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        var player = Global.getSector().characterData.person
        player.stats.officerNumber.modifyFlat("sc_officer_management", 2f)
    }

    override fun onDeactivation(data: SCData) {

        var player = Global.getSector().characterData.person
        player.stats.officerNumber.unmodify("sc_officer_management")

        CrewTraining.removeOfficersOverTheLimit()
    }

}