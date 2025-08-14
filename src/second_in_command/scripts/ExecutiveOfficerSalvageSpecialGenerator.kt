package second_in_command.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignTerrainAPI
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.impl.campaign.ids.Entities
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.SleeperPodsSpecial.SleeperPodsSpecialData
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.getSalvageSpecial
import org.magiclib.kotlin.setSalvageSpecial
import second_in_command.SCUtils
import second_in_command.interactions.ExecutiveOfficerRescueSpecial
import second_in_command.misc.SCSettings
import second_in_command.specs.SCAptitudeSpec
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore


class ExecutiveOfficerSalvageSpecialGenerator {

    fun generate() {

        if (SCUtils.isAssociatesBackgroundActive()) return //Don't add executives when this background is active
        if (SCSettings.derelictRarity == SCSettings.DerelictRarity.None) return

        var entitiesInSector = Global.getSector().starSystems.flatMap { it.customEntities }

        var entitiesWithPods = entitiesInSector.filter { isSalvagePod(it) }

        var percent = when(SCSettings.derelictRarity) {
            SCSettings.DerelictRarity.None -> 0f
            SCSettings.DerelictRarity.Rare -> 0.2f
            SCSettings.DerelictRarity.Normal -> 0.4f
            SCSettings.DerelictRarity.Common -> 0.6f
        }
        var maximum = (entitiesWithPods.count() * percent).toInt()

        var suitableEntities = entitiesInSector.filter { entity -> isSuitable(entity) }
        suitableEntities = suitableEntities.shuffled()




        var count = 0
        for (entity in suitableEntities) {
            count+=1
            if (count >= maximum) break

            var aptitudes = SCSpecStore.getAptitudeSpecs()
            var picker = WeightedRandomPicker<SCAptitudeSpec>()
            aptitudes.forEach { picker.add(it, it.getPlugin().getCryopodSpawnWeight(entity.starSystem)) }

            var pick = picker.pick()

            var officer = Global.getSector().playerFaction.createRandomPerson()
            var scOfficer = SCOfficer(officer, pick.id)

            scOfficer.increaseLevel(MathUtils.getRandomNumberInRange(1, 2))

            var special = ExecutiveOfficerRescueSpecial(scOfficer)
            entity.setSalvageSpecial(special)
            entity.addTag("sc_has_officer_special")
        }

    }

    fun isSalvagePod(entity: CustomCampaignEntityAPI) : Boolean {
        var special = entity.getSalvageSpecial() ?: return false
        return special::class.java == SleeperPodsSpecialData::class.java
    }

    fun isSuitable(entity: CustomCampaignEntityAPI) : Boolean {

        var special = entity.getSalvageSpecial()
        if (special != null) return false

        var type = entity.customEntityType

        // derelict ship
        if (entity.customPlugin is DerelictShipEntityPlugin || Entities.WRECK == type) return true

        // debris field
        if (entity is CampaignTerrainAPI && (entity as CampaignTerrainAPI).plugin is DebrisFieldTerrainPlugin) return true

        var validEntities = listOf<String>(
            Entities.STATION_MINING_REMNANT,
            Entities.STATION_RESEARCH_REMNANT,
            Entities.ORBITAL_HABITAT_REMNANT,
            Entities.STATION_MINING_REMNANT)

        if (validEntities.contains(type)) return true

        return false
    }

}