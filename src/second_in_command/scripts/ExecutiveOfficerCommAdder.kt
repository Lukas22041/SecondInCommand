package second_in_command.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.loading.ContactTagSpec
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.json.JSONObject
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.setSalvageSpecial
import second_in_command.SCUtils
import second_in_command.interactions.ExecutiveOfficerRescueSpecial
import second_in_command.misc.SCSettings
import second_in_command.specs.SCAptitudeSpec
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ExecutiveOfficerCommAdder : EconomyTickListener {

    var previousPeople = HashMap<String, MarketAPI>()


    override fun reportEconomyTick(iterIndex: Int) {

    }

    override fun reportEconomyMonthEnd() {

        if (SCUtils.isAssociatesBackgroundActive()) return //Don't add executives when this background is active

        var markets = Global.getSector().economy.marketsCopy

        for ((entry, market) in previousPeople) {
            market.commDirectory.removeEntry(entry)
        }

        previousPeople.clear()

        if (SCSettings.commRarity == SCSettings.CommRarity.None) return

        //var chance = 0.5f
        //var chance = 0.8f
        var chance = when(SCSettings.commRarity)  {
            SCSettings.CommRarity.None -> 0f
            SCSettings.CommRarity.Rare -> 0.35f
            SCSettings.CommRarity.Normal -> 0.8f
            SCSettings.CommRarity.Common -> 0.9f
        }
        for (market in markets) {
            if (market == null || market.isHidden) continue

            if (Random().nextFloat() >= chance) continue

            var count = 1
            if (Random().nextFloat() >= 0.55f && SCSettings.commRarity != SCSettings.CommRarity.Rare) count += 1
            if (Random().nextFloat() >= 0.75f && SCSettings.commRarity == SCSettings.CommRarity.Rare) count += 1
            if (Random().nextFloat() >= 0.6f && SCSettings.commRarity == SCSettings.CommRarity.Common) count += 1

            var alreadyPicked = ArrayList<String>()

            for (i in 0 until count) {
                var aptitudes = SCSpecStore.getAptitudeSpecs()
                var picker = WeightedRandomPicker<SCAptitudeSpec>()

                for (aptitude in aptitudes) {
                    if (alreadyPicked.contains(aptitude.id)) continue
                    var weight = aptitude.getPlugin().getMarketSpawnweight(market)
                    if (SCUtils.getPlayerData().hasAptitudeInFleet(aptitude.id)) weight *= 0.6f
                    picker.add(aptitude, weight)
                }

                if (picker.isEmpty) break

                //aptitudes.forEach { picker.add(it, it.getPlugin().getMarketSpawnweight(market)) }

                var pick = picker.pick()
                alreadyPicked.add(pick.id)

                var officer = market.faction.createRandomPerson()
                officer.setFaction(Factions.INDEPENDENT)

                officer.memoryWithoutUpdate.set("\$sc_officer_aptitude", pick.id)
                officer.memoryWithoutUpdate.set("\$sc_hireable", true)

                officer.postId = "executive_officer_${pick.id}"

                var id = market.commDirectory.addPerson(officer)
                previousPeople.put(id, market)

                var entry = market.commDirectory.getEntryForPerson(officer)
                entry.type
            }
        }



    }

}