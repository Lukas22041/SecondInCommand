package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.util.DelayedActionScript
import second_in_command.misc.baseOrModSpec
import second_in_command.skills.engineering.SolidConstructionIntel

class SCCampaignEventListener : BaseCampaignEventListener(false) {


    override fun reportBattleOccurred(primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {
        if (battle?.isPlayerInvolved == true) {
            //var side = battle.playerSide
            var data = SCUtils.getPlayerData() ?: return

            if (data.isSkillActive("sc_engineering_solid_construction")) {
                var dmodData = SCUtils.getSectorData().dmodData ?: return

                var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

                // Shipname/Hullmod Name
                var removed = ArrayList<Pair<String, String>>()

                for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {

                    var hmods = member.variant.permaMods
                    var dmods = dmodSpecs.filter { hmods.contains(it.id) }.map { it.id }.toMutableList()
                    var dmodsSaved = dmodData.get(member.id) ?: continue //Continue in case this ship hasnt been saved by the script before.
                    var dmodCount = dmods.count()

                    if (dmodCount <= 2) continue
                    for (dmod in ArrayList(dmods)) {
                        dmodCount = dmods.count() //Dont reduce below 2
                        if (!dmodsSaved.contains(dmod) && dmodCount > 2) {
                            dmods.remove(dmod)
                            DModManager.removeDMod(member.variant, dmod)
                            var spec = Global.getSettings().getHullModSpec(dmod)
                            removed.add(Pair("${member.shipName}", spec.displayName))
                        }
                    }

                    dmodData.put(member.id, dmods) //Update data immediately in case theres an immediate battle afterwards.

                }

                if (removed.isNotEmpty()) {
                    Global.getSector().addScript(object: DelayedActionScript(0.15f) {
                        override fun doAction() {
                            var intel = SolidConstructionIntel(removed)
                            Global.getSector().intelManager.addIntel(intel)
                        }
                    })
                }

            }
        }
    }

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