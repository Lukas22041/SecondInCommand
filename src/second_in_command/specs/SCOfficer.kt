package second_in_command.specs

import com.fs.starfarer.api.characters.PersonAPI

class SCOfficer(var person: PersonAPI, var aptitudeId: String) {


    var activeSkillIDs = mutableSetOf<String>()

    fun getAptitudeSpec() : SCAptitudeSpec {
        return SCSpecStore.getAptitudeSpec(aptitudeId)!!
    }

    fun getAptitudePlugin() : SCBaseAptitudePlugin {
        return getAptitudeSpec().getPlugin()
    }




    fun getSkillPlugins() {
        
    }


}