package second_in_command.skills.warfare

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.FighterLaunchBayAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.levelBetween
import second_in_command.misc.logger
import second_in_command.specs.SCBaseSkillPlugin

class RedundantBays : SCBaseSkillPlugin() {


    override fun getAffectsString(): String {
        return "all carriers in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("All fighters deployed by the ship can be instantly replaced once per battle", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {



    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

        if (!ship!!.hasListenerOfClass(ReduntantBaysListener::class.java)) {
            ship.addListener(ReduntantBaysListener(ship))
        }

    }

    override fun advanceInCombat(data: SCData?, ship: ShipAPI?, amount: Float?) {


        /*var usedBays = ship!!.customData.get("sc_reduntant_bays_used") as ArrayList<FighterLaunchBayAPI>?
        if (usedBays == null) {
            usedBays = ArrayList<FighterLaunchBayAPI>()
            ship.setCustomData("sc_reduntant_bays_used", usedBays)
        }

        for (bay in ship!!.launchBaysCopy) {
            if (bay.wing == null) continue

            var log = this.logger()

            log.debug(bay.timeUntilNextReplacement)

            var size = bay.wing.wingMembers.size
            if (size == 0) {
                var test = ""
            }

            if (usedBays.contains(bay)) continue

            usedBays.add(bay)



            val spec = bay.wing.spec

            bay.makeCurrentIntervalFast()
            bay.fastReplacements = bay.fastReplacements + spec.numFighters
        }*/

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {

        var carriers = fleet.fleetData.membersListCopy.filter { it.isCarrier || it.numFlightDecks >= 1 }
        if (carriers.isNotEmpty()) return super.getNPCSpawnWeight(fleet)

        return 0f
    }

}

class ReduntantBaysListener(var ship: ShipAPI) : AdvanceableListener {

    var availableReplacements = hashMapOf<FighterLaunchBayAPI, Int>()
    var delayedBays = hashMapOf<FighterLaunchBayAPI, Float>()


    override fun advance(amount: Float) {


        for (bay in ship.launchBaysCopy) {


            if (delayedBays.contains(bay)) {
                var delayed = delayedBays.get(bay)!! - 1f * amount
                delayedBays.set(bay, delayed)

                if (delayed > 0f) {
                    continue
                }
            }


            if (bay.wing == null) continue

            if (!availableReplacements.contains(bay)) {

                //Only consider this bay once it deployed all of its fighters atleast once
                if (bay.wing.wingMembers.size == bay.wing.spec.numFighters) {
                    availableReplacements.put(bay, bay.wing.spec.numFighters)
                }

            }

            if (!availableReplacements.contains(bay)) continue



            var left = availableReplacements.get(bay)!!

            var time = bay.timeUntilNextReplacement

            if (time > 0f && left > 0) {
                bay.makeCurrentIntervalFast()
                bay.fastReplacements = bay.fastReplacements + 1
                availableReplacements.put(bay, left-1)

                delayedBays.put(bay, 0.75f)
            }
        }

    }

}