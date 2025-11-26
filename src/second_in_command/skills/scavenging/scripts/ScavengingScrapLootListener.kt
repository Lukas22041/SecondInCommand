package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.listeners.ShowLootListener
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils

class ScavengingScrapLootListener : ShowLootListener {

    var lastScrapGainedForLootScreen: Int? = null

    override fun reportAboutToShowLootToPlayer(loot: CargoAPI?, dialog: InteractionDialogAPI?) {

        var data = SCUtils.getPlayerData()
        if (data.isAptitudeActive("sc_scavenging")) {
            var scrapGain = 20
            var manager = data.scrapManager
            scrapGain = MathUtils.clamp(scrapGain, 0, manager.getMaxScrap()-manager.getCurrentScrap())
            manager.adjustScrap(scrapGain)
            lastScrapGainedForLootScreen = scrapGain
        }


    }
}