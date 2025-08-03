package second_in_command.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.SCModPlugin
import second_in_command.SCUtils
import second_in_command.misc.SCSettings
import second_in_command.scripts.AutomatedShipsManager
import second_in_command.skills.PlayerLevelEffects

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

                    member.variant.addPermaMod("sc_skill_controller")

                    /*var moduleSlots = member.variant.moduleSlots
                    for (slot in moduleSlots) {
                        var module = member.variant.getModuleVariant(slot)
                        module.addMod("sc_skill_controller")
                    }*/
                }
            }
        }
    }


    override fun getDisplaySortOrder(): Int {
        return 0
    }

    override fun getDisplayCategoryIndex(): Int {
        return 0
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, id: String?) {


        //Dmod overlay
        if (SCSettings.reducedDmodOverlay) {
            if (!ship!!.variant.hasHullMod("comp_structure") && DModManager.getNumDMods(ship!!.variant) in 1..2) {
                ship.setDHullOverlay("graphics/damage/dmod_overlay_sic_very_light.png")
            }
        }



        var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }


        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsAfterShipCreation(data, ship, ship!!.variant, "${id}_${skill.getId()}")
        }

        if (data.isPlayer) {
            PlayerLevelEffects.applyEffectsAfterShipCreation(data, ship, ship!!.variant, "${id}_player")
        }

    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        var member = stats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (fleet != Global.getSector().playerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }

        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsBeforeShipCreation(data, stats, stats.variant, hullSize, "${id}_${skill.getId()}")
        }

        if (data.isPlayer) {
            PlayerLevelEffects.applyEffectsBeforeShipCreation(data, stats, stats.variant, hullSize!!, "${id}_player")
        }

        //Handle Automated Ships
        AutomatedShipsManager.get().applyEffects(data, stats, stats.variant, hullSize, "sc_automation_manager")
    }

    override fun applyEffectsToFighterSpawnedByShip(fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }

        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsToFighterSpawnedByShip(data, fighter, ship, "${id}_${skill.getId()}")
        }

        if (data.isPlayer) {
            PlayerLevelEffects.applyEffectsToFighterSpawnedByShip(data, fighter, ship, "${id}_player")
        }
    }

    override fun advanceInCampaign(member: FleetMemberAPI?, amount: Float) {
        var fleet = member?.fleetData?.fleet ?: return
        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        //Deprecated
        /*var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCampaign(data, member, amount)
        }*/

        if (data.isPlayer) {
            PlayerLevelEffects.advanceInCampaign(data, member, amount)
        }
    }


    override fun advanceInCombat(ship: ShipAPI?, amount: Float) {

       /* println()
        for (ship in Global.getCombatEngine().ships) {
            if (ship.owner == 0) continue
            if (ship.isFighter) continue
            var data = ship.fleetMember.fleetData
            println("${ship.fleetMember}_"+data)
            println(data?.fleet)
            println()
        }*/

        var member = ship?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }

        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        var skills = SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCombat(data, ship, amount)
        }

        if (data.isPlayer) {
            PlayerLevelEffects.advanceInCombat(data, ship, amount)
        }
    }
}