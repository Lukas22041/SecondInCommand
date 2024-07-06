package second_in_command.skills.automated

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.util.Misc
import second_in_command.specs.SCBaseAptitudePlugin

class AptitudeAutomated : SCBaseAptitudePlugin() {

    companion object {

    }

    override fun getOriginSkillId(): String {
        return "sc_automated_1"
    }

    override fun createSections() {


    }


}