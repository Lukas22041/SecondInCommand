package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.StatBonus
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard

//Manager for Scavenging Scrap system.
class ScrapManager(var fleet: CampaignFleetAPI) : EveryFrameScript {

    private var currentScrap: Int = 50
    private var maxScrap = StatBonus()

    private var scrapConsumptionThisFrame = 0
    private var keepConsumptionForFrame = false

    fun getMaxScrap() : Int {
        return maxScrap.computeEffective(100f).toInt();
    }

    fun getCurrentScrap() : Int {
        return currentScrap
    }

    fun adjustScrap(change: Int) {
        currentScrap += change;
        currentScrap = MathUtils.clamp(currentScrap, 0, getMaxScrap())
    }

    fun getScrapAboutToBeConsumed() : Int {
        return scrapConsumptionThisFrame
    }

    fun setScrapConsumptionThisFrame(consumption: Int) {
        scrapConsumptionThisFrame = consumption
        scrapConsumptionThisFrame = MathUtils.clamp(scrapConsumptionThisFrame, 0, getCurrentScrap())
        keepConsumptionForFrame = true
    }

    override fun advance(amount: Float) {

       /* if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            setScrapConsumptionThisFrame(30)
        }*/

        if (keepConsumptionForFrame) {
            keepConsumptionForFrame = false
        } else {
            scrapConsumptionThisFrame = 0;
        }
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }




}