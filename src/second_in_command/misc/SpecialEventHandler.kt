package second_in_command.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.util.DelayedActionScript
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import java.util.*

object SpecialEventHandler {

    //Always done on onGameLoad
    fun checkEvents() {
        checkChristmas()
    }

    fun checkChristmas() {
        var isChristmas = false

        val currentDate = Date()

        var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        var month = Calendar.getInstance().get(Calendar.MONTH)

        if (month == Calendar.DECEMBER && day in 16..27) {
            isChristmas = true
        }

        /*val startDate = Date(1734303600L * 1000) // 16th
        val endDate = Date(1735254000L * 1000) //27th
        if (startDate.before(currentDate) && endDate.after(currentDate)) {
            isChristmas = true
        }*/

        var data = SCUtils.getPlayerData()

        if (isChristmas) {
            if (data.getOfficersInFleet().none { it.aptitudeId == "sc_christmas" }) {
                var officer = SCUtils.createRandomSCOfficer("sc_christmas")
                officer.person.name = FullName("Kanta", "Klaus", FullName.Gender.MALE)

                var path = "graphics/secondInCommand/special/christmas/kanta.png"
                officer.person.portraitSprite = path

                officer.increaseLevel(4)
                officer.skillPoints = 0

                data.addOfficerToFleet(officer)

                Global.getSector().addScript(object : DelayedActionScript(0.5f) {
                    override fun doAction() {
                        Global.getSector().campaignUI.addMessage(" - A Festive Spirit has joined your command crew", Misc.getHighlightColor())
                    }
                })
            }

        } else {
            var officer = data.getOfficersInFleet().find {  it.aptitudeId == "sc_christmas" }
            if (officer != null) {

                data.removeOfficerFromFleet(officer)

                Global.getSector().addScript(object : DelayedActionScript(0.5f) {
                    override fun doAction() {
                        Global.getSector().campaignUI.addMessage(" - The Festive Spirit has departed from your fleet", Misc.getHighlightColor())
                    }
                })

            }
        }
    }

}