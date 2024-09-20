package second_in_command.skills.strikecraft

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.FighterLaunchBayAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class SwarmDeployment : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighter wings"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Fighter wings with at least four fighters gain an additional fighter in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This skill may not work on ships with the Reserve Deployment shipsystem, or others of the same kind", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Reserve Deployment")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) {

        var alreadyUsed = ship!!.customData.get("sc_swarm_deployment_used") as ArrayList<FighterLaunchBayAPI>?
        if (alreadyUsed == null) {
            alreadyUsed = ArrayList()
            ship.setCustomData("sc_swarm_deployment_used", alreadyUsed)
        }

        val minRate = Global.getSettings().getFloat("minFighterReplacementRate")

        val bays = ship!!.launchBaysCopy.size
        val cost = 0f

        for (bay in ship.launchBaysCopy) {
            if (bay.wing == null) continue
            if (bay.wing.spec.numFighters <= 3) continue
            if (alreadyUsed.contains(bay)) continue

           /* val rate = Math.max(minRate, bay.currRate - cost)
            bay.currRate = rate
            bay.makeCurrentIntervalFast()*/

            val spec = bay.wing.spec
            val addForWing = 1
            val maxTotal = spec.numFighters + addForWing
            //val actualAdd = maxTotal - bay.wing.wingMembers.size

            bay.fastReplacements = bay.fastReplacements + addForWing
            bay.extraDeployments = 99999 //How often an extra ship can be deployed
            bay.extraDeploymentLimit = maxTotal //how many extra ships are deployed at once
            bay.extraDuration = 9999999f

            alreadyUsed.add(bay)
        }

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {


    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {

    }

}