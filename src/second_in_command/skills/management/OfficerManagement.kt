package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.SCUtils.addAndCheckTag
import second_in_command.misc.randomAndRemove
import second_in_command.specs.SCBaseSkillPlugin

class OfficerManagement : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships with officers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
       /* tooltip.addPara("+2 to maximum number of officers you're able to command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this executive officer is unassigned, any officer over the limit will also be unassigned", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")
*/

        tooltip.addPara("All ships with officers gain the following bonuses: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The ships maximum combat readiness is increased by 5%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")
        tooltip.addPara("   - The ships peak performance time is increased by 20%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - The ships recovery cost is reduced by 20%%", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var captain = stats!!.fleetMember?.captain
        if (captain != null && !captain.isDefault) {
            stats.maxCombatReadiness.modifyFlat(id, 0.05f, "Officer Management")
            stats.peakCRDuration.modifyPercent(id, 20f)
            stats.suppliesToRecover.modifyMult(id, 0.8f)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {
        //data.commander.stats.officerNumber.modifyFlat("sc_officer_management", 2f)
    }

    override fun onActivation(data: SCData) {

       /* //Add additional officers
        if (data.isNPC && !data.fleet.addAndCheckTag("sc_officer_management_update")) {
            var count = 2
            var membersWithoutOfficers = data.fleet.fleetData.membersListCopy
                .filter { (it.captain == null || it.captain.isDefault) && !it.isAutomated() }.toMutableList()


            for (i in 0 until 2) {
                //var officer = data.fleet.faction.createRandomPerson()
                var level = MathUtils.getRandomNumberInRange(2, 5)
                var officer = OfficerManagerEvent.createOfficer(data.faction, level)

                if (membersWithoutOfficers.isNotEmpty()) {
                    var pick = membersWithoutOfficers.randomAndRemove()
                    pick.captain = officer
                }
            }
        }*/

    }

    override fun onDeactivation(data: SCData) {

      /*  data.commander.stats.officerNumber.unmodify("sc_officer_management")

        if (!data.isNPC) {
            CrewTraining.removeOfficersOverTheLimit()
        }*/

    }


}