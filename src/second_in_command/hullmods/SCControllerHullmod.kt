package second_in_command.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils

class SCControllerHullmod : BaseHullMod() {

    companion object {
        fun ensureAddedControllerToFleet() {
            var playerfleet = Global.getSector().playerFleet ?: return
            if (playerfleet.fleetData?.membersListCopy == null) return
            for (member in playerfleet.fleetData.membersListCopy) {
                if (!member.variant.hasHullMod("sc_skill_controller")) {
                    if (member.variant.source != VariantSource.REFIT) {
                        var variant = member.variant.clone();
                        variant.originalVariant = null;
                        variant.hullVariantId = Misc.genUID()
                        variant.source = VariantSource.REFIT
                        member.setVariant(variant, false, true)
                    }

                    member.variant.addMod("sc_skill_controller")

                    var moduleSlots = member.variant.moduleSlots
                    for (slot in moduleSlots) {
                        var module = member.variant.getModuleVariant(slot)
                        module.addMod("sc_skill_controller")
                    }
                }
            }
        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, id: String?) {
        var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsAfterShipCreation(data, ship, ship!!.variant, "${id}_${skill.getId()}")
        }
    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        var member = stats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsBeforeShipCreation(data, stats, stats!!.variant, hullSize, "${id}_${skill.getId()}")
        }
    }

    override fun applyEffectsToFighterSpawnedByShip(fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsToFighterSpawnedByShip(data, fighter, ship, "${id}_${skill.getId()}")
        }
    }

    override fun advanceInCampaign(member: FleetMemberAPI?, amount: Float) {
        var fleet = member?.fleetData?.fleet ?: return
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCampaign(data, member, amount)
        }
    }

    override fun advanceInCombat(ship: ShipAPI?, amount: Float) {
        var member = ship?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCombat(data, ship, amount)
        }
    }
}