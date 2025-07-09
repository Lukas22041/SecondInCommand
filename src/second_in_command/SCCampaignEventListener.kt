package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext

//No longer used
class SCCampaignEventListener : BaseCampaignEventListener(false) {


    override fun reportEncounterLootGenerated(plugin: FleetEncounterContextPlugin?, loot: CargoAPI?) {
       /* if (plugin !is FleetEncounterContext) return



        //Largely copied from FleetEncounterContext
        if (plugin.battle.isPlayerSide(plugin.battle.getSideFor(plugin.winner))) {



            var fpTotal = 0
            for (data in plugin.loserData.ownCasualties) {
                var fp = data.member.fleetPointCost.toFloat()
                fp *= 1f + data.member.captain.stats.level / 5f
                fpTotal += fp.toInt()
            }

            var xp = fpTotal.toFloat() * 250f
            xp *= 2f

            val difficultyMult = Math.max(1f, plugin.difficulty)
            xp *= difficultyMult

            xp *= plugin.computePlayerContribFraction()

            //xp *= Global.getSettings().getFloat("xpGainMult")

            for (officer in SCUtils.getPlayerData().getOfficersInFleet()) {
                officer.addXP(xp)
            }

        }*/

    }
}