package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.StatBonus
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.SCUtils

class AutomatedShipsManager : EveryFrameScript {

    companion object {
        @JvmStatic
        fun get() : AutomatedShipsManager {
            if (Global.getCurrentState() == GameState.TITLE) return AutomatedShipsManager()
            var manager = Global.getSector()?.transientScripts?.find { it is AutomatedShipsManager } as AutomatedShipsManager?

            if (manager == null) {
                manager = AutomatedShipsManager()
                Global.getSector()?.transientScripts?.add(manager)
            }

            return manager
        }
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    var MAX_CR_BONUS = 100f

    override fun advance(amount: Float) {

        var player = Global.getSector().playerPerson
        var stat = getAutoDPStat()

        if (canAcquireAutoShips()) {
            Misc.getAllowedRecoveryTags().add(Tags.AUTOMATED_RECOVERABLE)
        } else {
            Misc.getAllowedRecoveryTags().remove(Tags.AUTOMATED_RECOVERABLE)
        }
    }

    //Called from controller hullmod
    fun applyEffects(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats) && !Automated.isAutomatedNoPenalty(stats)) {

            var automatedDP = getUsedDP()
            var maxPoints = getMaximumDP()
            var bonus = SCUtils.computeThresholdBonus(automatedDP, MAX_CR_BONUS, maxPoints)

            stats!!.maxCombatReadiness.modifyFlat(id, bonus * 0.01f, "Automated ship skills")
        }
    }



    fun canAcquireAutoShips() : Boolean {
        return getMaximumDP() >= 1f
    }

    fun getMaximumDP() : Float {
        return getAutoDPStat().computeEffective(0f)
    }

    fun getUsedDP() : Float{
        if (Global.getCurrentState() == GameState.TITLE) return 0f
        var fleet = Global.getSector()?.playerFleet ?: return 0f //Can be null during Deserialization :)
        var points = 0f


        for (curr in fleet.fleetData.membersListCopy) {
            if (curr.isMothballed) continue
            if (!Misc.isAutomated(curr)) continue
            if (Automated.isAutomatedNoPenalty(curr)) continue
            var mult = 1f
            //if (curr.getCaptain().isAICore()) {
            points += curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE)
            mult = curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT)


            if (mult == 0f) mult = 1f
            //}

            var memberMult = curr.stats.dynamic.getStat("sc_auto_points_mult").modifiedValue
            mult *= memberMult


            points += Math.round(curr.deploymentPointsCost * mult).toFloat()
        }
        return Math.round(points).toFloat()
    }

    fun getAutoDPStat() : StatBonus {
        return Global.getSector().playerPerson.stats.dynamic.getMod("sc_auto_dp")
    }

}