package second_in_command

import com.fs.starfarer.api.Global
import second_in_command.specs.SCOfficer

object SCUtils {

    var MOD_ID = "second_in_command"

    fun getSCData() : SCData {
        var data = Global.getSector().memoryWithoutUpdate.get("\$sc_stored_data") as SCData?
        if (data == null) {
            data = SCData()
            Global.getSector().memoryWithoutUpdate.set("\$sc_stored_data", data)
        }
        return data
    }

    fun createRandomSCOfficer(aptitudeId: String) : SCOfficer {
        var person = Global.getSector().playerFaction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }


}