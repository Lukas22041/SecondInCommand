package second_in_command.skills.technology

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.SCSettings
import second_in_command.skills.automated.AutomatedShips
import second_in_command.skills.automated.SCBaseAutoPointsSkillPlugin
import second_in_command.specs.SCBaseSkillPlugin

class MakeshiftDrones : SCBaseAutoPointsSkillPlugin() {
    override fun getProvidedPoints(): Int {
        return (90 * SCSettings.autoPointsMult).toInt()
    }
}