package second_in_command.skills.warfare

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import second_in_command.SCData
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeWarfare : SCBaseAptitudePlugin() {

    override fun getOriginSkillId(): String {
        return "sc_warfare_iron_sight"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "combat2")
        section1.addSkill("sc_warfare_reserve_thrusters")
        section1.addSkill("sc_warfare_overprepared")
        section1.addSkill("sc_warfare_stabilised_targeting")
        section1.addSkill("sc_warfare_immovable_object")
        section1.addSkill("sc_warfare_surefire_impact")
        section1.addSkill("sc_warfare_tenacity")
        section1.addSkill("sc_warfare_redundant_bays")

        addSection(section1)

        var section2 = SCAptitudeSection(false, 4, "combat4")
        section2.addSkill("sc_warfare_deflective_plating")
        section2.addSkill("sc_warfare_overwhelming_force")
        addSection(section2)



    }

    var factionsToPick = listOf(Factions.HEGEMONY, Factions.LUDDIC_CHURCH, Factions.LUDDIC_PATH)
    var factionsNotToPick = listOf(Factions.TRITACHYON, Factions.OMEGA)

    override fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI)  : Float {
        var mult = 1f

        if (factionsToPick.contains(fleet.faction.id)) mult *= 1.5f
        if (factionsNotToPick.contains(fleet.faction.id)) mult *= 0.5f

        return mult
    }



}