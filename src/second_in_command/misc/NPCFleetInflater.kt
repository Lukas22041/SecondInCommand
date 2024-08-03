package second_in_command.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetInflater
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils

class NPCFleetInflater : FleetInflationListener {
    override fun reportFleetInflated(fleet: CampaignFleetAPI, inflater: FleetInflater?) {
        if (fleet != Global.getSector().playerFleet) {

            //Create Data if the fleet has none yet
            var data = SCUtils.getFleetData(fleet)

            if (fleet.fleetData?.membersListCopy == null) return
            for (member in fleet.fleetData.membersListCopy) {
                if (!member.variant.hasHullMod("sc_skill_controller")) {
                    if (member.variant.source != VariantSource.REFIT) {
                        var variant = member.variant.clone();
                        variant.originalVariant = null;
                        variant.hullVariantId = Misc.genUID()
                        variant.source = VariantSource.REFIT
                        member.setVariant(variant, false, true)
                    }

                    member.variant.addMod("sc_skill_controller")

                    var moduleSlots = member.variant.moduleSlots
                    for (slot in moduleSlots) {
                        var module = member.variant.getModuleVariant(slot)
                        module.addMod("sc_skill_controller")
                    }
                }
            }
        }
    }
}