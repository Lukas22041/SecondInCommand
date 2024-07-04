package second_in_command

import com.fs.starfarer.api.Global
import second_in_command.specs.SCOfficer

object SCUtils {

    var MOD_ID = "second_in_command"
    var DATA_KEY = "\$sc_stored_data"

    fun getSCData() : SCData {
        var data = Global.getSector().characterData.memoryWithoutUpdate.get(DATA_KEY) as SCData?
        if (data == null) {
            data = SCData(Global.getSector().characterData.person)
            Global.getSector().characterData.memoryWithoutUpdate.set(DATA_KEY, data)
        }
        return data
    }

    fun createRandomSCOfficer(aptitudeId: String) : SCOfficer {
        var person = Global.getSector().playerFaction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }


}