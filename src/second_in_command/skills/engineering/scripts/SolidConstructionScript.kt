package second_in_command.skills.engineering.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.util.IntervalUtil
import second_in_command.SCUtils

class SolidConstructionScript : EveryFrameScript {

    var interval = IntervalUtil(0.2f, 0.25f)

    override fun isDone(): Boolean {
        return false
    }


    override fun runWhilePaused(): Boolean {
        return false
    }

    //Keep track of dmods currently on the ship, then in SCCampaignEventListener check if there have been new dmods after a battle finished.
    override fun advance(amount: Float) {

        interval.advance(amount)
        if (interval.intervalElapsed()) {
            var data = SCUtils.getSectorData()
            var dmodData = data.dmodData

            var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

            for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {

                var hmods = member.variant.permaMods
                var dmods = dmodSpecs.filter { hmods.contains(it.id) }.map { it.id }

                dmodData.put(member.id, dmods)
            }
        }
    }
}