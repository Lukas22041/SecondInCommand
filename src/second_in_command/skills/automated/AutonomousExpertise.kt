package second_in_command.skills.automated

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.SCSettings
import second_in_command.specs.SCBaseSkillPlugin

class AutonomousExpertise : SCBaseAutoPointsSkillPlugin() {
    override fun getProvidedPoints(): Int {
        return (60 * SCSettings.autoPointsMult).toInt()
    }
}