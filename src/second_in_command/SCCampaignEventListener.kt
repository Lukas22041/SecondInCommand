package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import org.lazywizard.lazylib.MathUtils
import second_in_command.misc.baseOrModSpec
import second_in_command.misc.levelBetween

class SCCampaignEventListener : BaseCampaignEventListener(false) {


    override fun reportEncounterLootGenerated(plugin: FleetEncounterContextPlugin?, loot: CargoAPI?) {
        super.reportEncounterLootGenerated(plugin, loot)

        var defeated = plugin!!.loserData.destroyedInLastEngagement + plugin.loserData.disabledInLastEngagement
        var totalFP = defeated.sumOf { it.baseOrModSpec().fleetPoints }

        var level = totalFP.toFloat().levelBetween(0f, 400f)

        var data = SCUtils.getSCData()

        for (officer in data.getOfficersInFleet()) {
            var xp = MathUtils.getRandomNumberInRange(2000f, 2500f)
            officer.addXP(xp * level)
        }


    }
}