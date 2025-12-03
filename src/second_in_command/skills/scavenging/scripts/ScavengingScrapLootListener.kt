package second_in_command.skills.scavenging.scripts

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.listeners.ShowLootListener
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl
import com.fs.starfarer.api.impl.campaign.ids.Entities
import com.fs.starfarer.api.impl.campaign.ids.Tags
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils
import second_in_command.skills.scavenging.HyperspatialDrifter

class ScavengingScrapLootListener : ShowLootListener {


    companion object {
        var ALREADY_LOOTED_KEY = "\$scavenging_entity_looted"
    }

    override fun reportAboutToShowLootToPlayer(loot: CargoAPI?, dialog: InteractionDialogAPI?) {
        var interactionTarget = dialog?.interactionTarget ?: return

        if (interactionTarget is CampaignFleetAPI) return //Fleets are handled within ScavengingScrapLootFromBattleListener
        if (interactionTarget.memoryWithoutUpdate.contains(ALREADY_LOOTED_KEY)) return

        var data = SCUtils.getPlayerData()
        if (data.isAptitudeActive("sc_scavenging")) {

            var id = interactionTarget.customEntitySpec?.id ?: ""
            var scrapGain = when(id) {
                Entities.STATION_RESEARCH, Entities.STATION_RESEARCH_REMNANT -> MathUtils.getRandomNumberInRange(22f, 25f)
                Entities.STATION_MINING, Entities.STATION_MINING_REMNANT -> MathUtils.getRandomNumberInRange(18f, 22f)
                Entities.ORBITAL_HABITAT, Entities.ORBITAL_HABITAT_REMNANT -> MathUtils.getRandomNumberInRange(18f, 22f)
               "IndEvo_arsenalStation", "IndEvo_orbitalLaboratory" -> MathUtils.getRandomNumberInRange(18f, 22f)
                Entities.DEBRIS_FIELD_SHARED -> MathUtils.getRandomNumberInRange(8f, 12f)
                Entities.WRECK -> MathUtils.getRandomNumberInRange(4f, 6f)
                Entities.CARGO_PODS, Entities.CARGO_POD_SPECIAL -> 0f
                else -> MathUtils.getRandomNumberInRange(5f, 10f)
            }

            if (data.isSkillActive("sc_scavenging_hyperspatial_drifter")) {
                scrapGain += MathUtils.getRandomNumberInRange(HyperspatialDrifter.SCRAP_EXTRA_PER_SALVAGE_MIN, HyperspatialDrifter.SCRAP_EXTRA_PER_SALVAGE_MAX)
            }

            var manager = data.scrapManager
            scrapGain = MathUtils.clamp(scrapGain, 0f, manager.getMaxScrap()-manager.getCurrentScrap())
            manager.adjustScrap(scrapGain)
            interactionTarget.memoryWithoutUpdate.set(ScavengingLootScreenModifierScript.SCAVENGING_SCRAP_KEY, scrapGain)
            interactionTarget.memoryWithoutUpdate.set(ALREADY_LOOTED_KEY, true)
            //lastScrapGainedForLootScreen = scrapGain

          /*  if (dialog is FleetInteractionDialogPluginImpl) {
                var test = ""
            }*/
        }


    }
}