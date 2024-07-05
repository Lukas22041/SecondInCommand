package second_in_command.specs

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI

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

    abstract fun addTooltip(tooltip: TooltipMakerAPI)

    open fun shouldAffectShip(spec: ShipHullSpecAPI, variant: ShipVariantAPI, hullSize: HullSize) : Boolean { return true }

    open fun applyEffectsAfterShipCreation(ship: ShipAPI?, variant: ShipVariantAPI, id: String?) { }

    open fun applyEffectsBeforeShipCreation(stats: MutableShipStatsAPI?, variant: ShipVariantAPI,  hullSize: HullSize?, id: String?) { }

    open fun advanceInCampaign(member: FleetMemberAPI?, amount: Float) { }

    open fun advanceInCombat(ship: ShipAPI?, amount: Float) { }

    open fun onActivation() { }

    open fun onDeactivation() { }
}