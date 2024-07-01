package second_in_command

import com.fs.starfarer.api.Global
import second_in_command.specs.SCOfficer

object SCUtils {

    var MOD_ID = "second_in_command"

    fun createRandomSCOfficer(aptitudeId: String) : SCOfficer {
        var person = Global.getSector().playerFaction.createRandomPerson()
        var officer = SCOfficer(person, aptitudeId)
        return officer
    }
}