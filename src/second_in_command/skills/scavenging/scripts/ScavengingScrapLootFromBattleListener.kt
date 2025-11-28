package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.campaign.BaseCampaignEventListener
import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils
import second_in_command.misc.levelBetween

class ScavengingScrapLootFromBattleListener : BaseCampaignEventListener(false) {

    companion object {
    }

    override fun reportEncounterLootGenerated(plugin: FleetEncounterContextPlugin?, loot: CargoAPI?) {
         if (plugin !is FleetEncounterContext) return

        var data = SCUtils.getPlayerData()

        if (data.isAptitudeActive("sc_scavenging")) {
            if (plugin.battle.isPlayerSide(plugin.battle.getSideFor(plugin.winner))) {

                var fpTotal = 0f
                for (data in plugin.loserData.ownCasualties) {
                    var fp = data.member.fleetPointCost.toFloat()
                    //fp *= 1f + data.member.captain.stats.level / 5f
                    fpTotal += fp
                }

                var maxFPRequired = 300f

                var min = 5
                var scaled = (30f+MathUtils.getRandomNumberInRange(2, 5)) * fpTotal.levelBetween(0f, maxFPRequired)
                var scrapGain = min+scaled

                //Get already set value, might be important for fights where you did pursuit the target
                var current = plugin.loser.memoryWithoutUpdate.getFloat(ScavengingLootScreenModifierScript.SCAVENGING_SCRAP_KEY) ?: 0f

                //Reduce result in that case to keep numbers more sane
                var extraModActive = current > 20f
                if (extraModActive) scrapGain *= 0.5f

                scrapGain+=current

                var manager = data.scrapManager
                scrapGain = MathUtils.clamp(scrapGain, 3f, 40f)
                scrapGain = MathUtils.clamp(scrapGain, 0f, manager.getMaxScrap()-manager.getCurrentScrap())

                plugin.loser.memoryWithoutUpdate.set(ScavengingLootScreenModifierScript.SCAVENGING_SCRAP_KEY, scrapGain)

                manager.adjustScrap(scrapGain)
            }
        }



    }
}