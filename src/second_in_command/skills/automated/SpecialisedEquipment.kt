package second_in_command.skills.automated

import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.skills.BestOfTheBest
import second_in_command.SCData

class SpecialisedEquipment : SCBaseAutoPointsSkillPlugin() {
    override fun getProvidedPoints(): Int {
        return 60
    }

    override fun advance(data: SCData, amunt: Float?) {
        super.advance(data, amunt)
    }
}