package second_in_command.specs

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData

/**Base Plugin for Skills.
 * Handles similar to hullmod plugins.
 * Do not store variables in the class itself. It will cause leaks.*/
abstract class SCBaseSkillPlugin {

    lateinit var spec: SCSkillSpec

    fun getId() : String{
        return spec.id
    }

    fun getIconPath() : String{
        return spec.iconPath
    }

    open fun getName() : String {
        return spec.name
    }

    abstract fun getAffectsString() : String

    abstract fun addTooltip(data: SCData, tooltip: TooltipMakerAPI)

    open fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) { }

    open fun applyEffectsBeforeShipCreation(data: SCData,stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: HullSize?,id: String?) { }

    open fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) { }

    open fun advanceInCampaign(data: SCData, member: FleetMemberAPI?, amount: Float) { }

    /**Non-ship specific campaign advance*/
    open fun advance(data: SCData, amount: Float) { }

    open fun advanceInCombat(data: SCData, ship: ShipAPI?, amount: Float) { }

    /**Called when the skill is acquired and if the officer is re-assigned. Also may be called in other scenarios aslong as the skill is active*/
    open fun onActivation(data: SCData) { }

    /**Called when the corrosponding officer is un-assigned */
    open fun onDeactivation(data: SCData) { }
}