package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.StatBonus
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import second_in_command.SCData
import second_in_command.SCUtils

//Manager for Scavenging Scrap system.
class ScrapManager(var data: SCData, var fleet: CampaignFleetAPI) : EveryFrameScript {

    private var currentScrap: Float = 50f
    //private var maxScrap = StatBonus()

    private var scrapConsumptionThisFrame = 0f
    private var keepConsumptionForFrame = false

    fun getMaxScrap() : Float {
        var max = 100f
        if (data.isSkillActive("sc_scavenging_scrapheap")) max += 50f
        return max
    }

    fun getCurrentScrap() : Float {
        return currentScrap
    }

    fun adjustScrap(change: Float) {
        currentScrap += change;
        currentScrap = MathUtils.clamp(currentScrap, 0f, getMaxScrap())
    }

    fun getScrapAboutToBeConsumed() : Float {
        return scrapConsumptionThisFrame
    }

    fun setScrapConsumptionThisFrame(consumption: Float) {
        scrapConsumptionThisFrame = consumption
        scrapConsumptionThisFrame = MathUtils.clamp(scrapConsumptionThisFrame, 0f, getCurrentScrap())
        keepConsumptionForFrame = true
    }

    override fun advance(amount: Float) {

       /* if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            setScrapConsumptionThisFrame(30)
        }*/

        //Decay scrap when inactive
        if (!data.isAptitudeActive("sc_scavenging")) {
            var days = Global.getSector().clock.convertToDays(amount)
            adjustScrap(-1f * days)
        }

        if (keepConsumptionForFrame) {
            keepConsumptionForFrame = false
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