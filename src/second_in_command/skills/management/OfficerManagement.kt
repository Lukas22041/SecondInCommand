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
        data.commander.stats.officerNumber.modifyFlat("sc_officer_management", 2f)
    }

    override fun onActivation(data: SCData) {

        //Add additional officers
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
        }

    }

    override fun onDeactivation(data: SCData) {

        data.commander.stats.officerNumber.unmodify("sc_officer_management")

        if (!data.isNPC) {
            CrewTraining.removeOfficersOverTheLimit()
        }

    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.flagship?.isAutomated() == true) return 0f
        return super.getNPCSpawnWeight(fleet)
    }

}