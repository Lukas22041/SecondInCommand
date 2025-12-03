package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.StatBonus
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector2f
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.getParent
import second_in_command.misc.levelBetween
import second_in_command.skills.scavenging.HyperspatialDrifter
import second_in_command.skills.scavenging.MakeshiftMeasures
import second_in_command.specs.SCOfficer
import java.util.HashMap
import java.util.Random

//Manager for Scavenging Scrap system.
class ScrapManager(var data: SCData) : EveryFrameScript {

    //private var currentScrap: Float = 0f
    //private var maxScrap = StatBonus()

    var scrapMap = HashMap<SCOfficer, Float>()

    private var scrapConsumptionThisFrame = 0f
    private var keepConsumptionForFrames = 3

    var reEvaluateRandom: Random = Random()
    var lastHyperspaceLoc: Vector2f? = null
    var scrapGainInterval = IntervalUtil(0.25f, 0.25f)

    fun readResolve() : ScrapManager {
        if (reEvaluateRandom == null) reEvaluateRandom = Random()
        if (scrapMap == null) scrapMap = HashMap()
        scrapGainInterval = IntervalUtil(0.25f, 0.25f)
        return this
    }

    fun getMaxScrap() : Float {
        var max = 100f
        if (data.isSkillActive("sc_scavenging_scrapheap")) max += 50f
        return max
    }

    fun getCurrentScrap() : Float {
        var officer = data.getActiveOfficers().find { it.aptitudeId == "sc_scavenging" }
        if (officer == null) return 0f //No scrap if you do not have a scavenging XO
        var value = scrapMap.get(officer)
        if (value == null) {
            value = 0f;
            scrapMap.set(officer, value);
        }
        return value
    }

    fun adjustScrap(change: Float) {
        var officer = data.getActiveOfficers().find { it.aptitudeId == "sc_scavenging" }
        if (officer == null) return //No scrap change if you do not have a scavenging XO
        adjustScrap(officer, change)
    }

    fun adjustScrap(officer: SCOfficer, change: Float) {
        var currentScrap = scrapMap.get(officer)
        if (currentScrap == null) {
            currentScrap = 0f;
            //scrapMap.set(officer, currentScrap);
        }

        currentScrap += change;
        currentScrap = MathUtils.clamp(currentScrap, 0f, getMaxScrap())
        scrapMap.set(officer, currentScrap);
    }

    fun getScrapAboutToBeConsumed() : Float {
        return scrapConsumptionThisFrame
    }

    fun setScrapConsumptionThisFrame(consumption: Float) {
        scrapConsumptionThisFrame = consumption
        scrapConsumptionThisFrame = MathUtils.clamp(scrapConsumptionThisFrame, 0f, getCurrentScrap())
        keepConsumptionForFrames = 3
    }

    override fun advance(amount: Float) {

       /* if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            setScrapConsumptionThisFrame(30)
        }*/

        //Decay scrap when inactive
       /* if (!data.isAptitudeActive("sc_scavenging")) {
            var days = Global.getSector().clock.convertToDays(amount)
            adjustScrap(-1f * days)
        } else {

        }*/

        //Scrap decay per officer
        var days = Global.getSector().clock.convertToDays(amount)
        for (data in scrapMap) {
            if (!data.key.isAssigned()) {
                adjustScrap(data.key, -1f * days)
            }
        }

        var officer = data.getActiveOfficers().find { it.aptitudeId == "sc_scavenging" }
        var isHyperActive = officer?.activeSkillIDs?.contains("sc_scavenging_hyperspatial_drifter") ?: false
        var isMakeshiftActive = data.fleet.memoryWithoutUpdate.get(MakeshiftMeasures.IS_ACTIVE_KEY) == true

        if (!data.fleet.isInHyperspace || !isHyperActive || isMakeshiftActive) {
            lastHyperspaceLoc = null
        }
        else if (lastHyperspaceLoc != null) {

            scrapGainInterval.advance(amount)
            if (scrapGainInterval.intervalElapsed()) {
                var loc = data.fleet.locationInHyperspace
                var distLY = Misc.getDistanceLY(lastHyperspaceLoc, loc)
                var level = distLY.levelBetween(0f, 2f)

                adjustScrap(HyperspatialDrifter.SCRAP_PER_LIGHTYEAR.toFloat() * level)

                lastHyperspaceLoc = Vector2f(loc)
            }

        }
        else if (data.fleet.isInHyperspace) {
            lastHyperspaceLoc = data.fleet.locationInHyperspace
        }

        if (keepConsumptionForFrames != 0) {
            keepConsumptionForFrames -= 1
        } else {
            scrapConsumptionThisFrame = 0f;
        }
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }




}