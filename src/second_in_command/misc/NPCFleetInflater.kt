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
            if (SCSettings.canNPCsSpawnWithSkills) {
                //Create Data if the fleet has none yet

                var data = SCUtils.getFleetData(fleet)
            }
        }
    }
}