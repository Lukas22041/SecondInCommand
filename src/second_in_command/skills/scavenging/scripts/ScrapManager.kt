package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.StatBonus
import org.lazywizard.lazylib.MathUtils

//Manager for Scavenging Scrap system.
class ScrapManager(var fleet: CampaignFleetAPI) : EveryFrameScript {

    private var currentScrap: Int = 50
    private var maxScrap = StatBonus()

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

    fun getScrapAboutToBeConsumed() {

    }

    override fun advance(amount: Float) {

    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }




}