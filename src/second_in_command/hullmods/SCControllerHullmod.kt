package second_in_command.hullmods

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import org.apache.log4j.Logger
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.SCSettings
import second_in_command.scripts.AutomatedShipsManager
import second_in_command.scripts.SicMidCombatAdder2
import second_in_command.skills.PlayerLevelEffects

class SCControllerHullmod : BaseHullMod() {
    companion object {
        val log: Logger? = Global.getLogger(SCControllerHullmod::class.java)
        var secOverrideKey = "SiC_SkillsOverrider";
        var noSkillTagHullmodID = "sc_no_skill";
        fun getFleetData(ship: ShipAPI?) : SCData? {
            //log?.info("attempting to get fleet data for "+ship?.name+", id: "+ship?.id)
            //log?.info("has custom data: "+ship?.customData?.contains(secOverrideKey));
            if (ship?.customData?.contains(secOverrideKey) == true) return ship.customData.get(secOverrideKey) as SCData;
            //log?.info("has fleet member: "+(ship?.mutableStats?.fleetMember?.fleetData?.fleet != null));


            var member = ship?.fleetMember;
            if (member == null) member = ship?.mutableStats?.fleetMember
            var fleet = member?.fleetData?.fleet
            if (member != null && fleet != null) {
                if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
                    //Fix for battles where you join an ally, as those set the members fleet to theirs.
                    fleet = Global.getSector().playerFleet
                }
                if (fleet != null && fleet.fleetData != null) return SCUtils.getFleetData(fleet)
            }
            //log?.info("has nothing. ship name: "+ship?.name)
            return null;
        }
        fun addHullmodAfterShipCreation(ship: ShipAPI?,  data: SCData?){
            //log?.info("seeing if I can add ship of; name: "+ship?.name+" id: "+ship?.id);
            if (ship == null || ship.fleetMember == null || ship.fleetMember.variant == null) return//for command shuttle
            //ship.getFleetMember().setCustomData(NANO_THIEF_SIC_HULLMOD_FLEET_KEY,fleet);
            //log?.info("adding ship; name: "+ship?.name+" id: "+ship?.id);
            if (ship?.variant?.hasHullMod("sc_skill_controller") == false){
                //log?.info("-adding hullmod...")
                val OVERWRITER = ship.variant //Global.getSettings().getVariant("Abyssal_XO_ReclaimCore_Blank").clone();
                OVERWRITER.source = VariantSource.REFIT
                //OVERWRITER.setWingId(0,skills.stats.OF_fighterToBuild);
                OVERWRITER.addMod("sc_skill_controller")
                //ship.getVariant().getHullMods();

                ship.fleetMember.setVariant(OVERWRITER, false, true) //setVariant(OVERWRITER,false,true);
            }
            ship?.setCustomData(secOverrideKey,data);

            var id = "sc_skill_controller_";
            //log?.info("-running skills (before)");
            for (skill in data!!.getAllActiveSkillsPlugins()) {
                skill.applyEffectsBeforeShipCreation(
                    data,
                    ship?.fleetMember?.stats,
                    ship?.variant,
                    ship?.hullSize,
                    "${id}_${skill.id}"
                )
            }
            //log?.info("-running skills (after)");
            for (skill in data.getAllActiveSkillsPlugins()) {
                skill.applyEffectsAfterShipCreation(
                    data,
                    ship,
                    ship?.variant,
                    "${id}_${skill.id}"
                )
            }
        }
        fun addHullmodAfterShipCreation(ship: ShipAPI?,  fleet: CampaignFleetAPI?){
            if (fleet == null) return;
            addHullmodAfterShipCreation(ship,SCUtils.getFleetData(fleet));
        }
        fun ensureAddedControllerToFleet() {
            var playerfleet = Global.getSector().playerFleet ?: return
            if (playerfleet.fleetData?.membersListCopy == null) return
            for (member in playerfleet.fleetData.membersListCopy) {
                if (!member.variant.hasHullMod("sc_skill_controller") && !member.variant.hasHullMod(noSkillTagHullmodID) && !member.hullSpec.hasTag(noSkillTagHullmodID)) {
                    if (member.variant.source != VariantSource.REFIT) {
                        var variant = member.variant.clone();
                        variant.originalVariant = null;
                        variant.hullVariantId = Misc.genUID()
                        variant.source = VariantSource.REFIT
                        member.setVariant(variant, false, true)
                    }

                    member.variant.addPermaMod("sc_skill_controller")
                    //addChilds(member.variant);
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
        //if (!Global.getCombatEngine().hasPluginOfClass(SiCMidCombatAdder::class.java)){
        //Dmod overlay
        if (SCSettings.reducedDmodOverlay) {
            if (!ship!!.variant.hasHullMod("comp_structure") && DModManager.getNumDMods(ship!!.variant) in 1..2) {
                ship.setDHullOverlay("graphics/damage/dmod_overlay_sic_very_light.png")
            }
        }



        /*var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }*/

        //var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = getFleetData(ship) ?: return;//SCUtils.getFleetData(fleet)
        var skills = data.getAllActiveSkillsPlugins();//SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsAfterShipCreation(data, ship, ship!!.variant, "${id}_${skill.getId()}")
        }

        if (data.isPlayer && ship != null) {
            PlayerLevelEffects.applyEffectsAfterShipCreation(data, ship, ship.variant, "${id}_player")
        }

    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        var member = stats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return
        if (/*Global.getCurrentState() == GameState.COMBAT && */!Global.getCombatEngine().listenerManager.hasListenerOfClass(SicMidCombatAdder2::class.java)){
            log?.info("ADDING THE LISTINER")
            Global.getCombatEngine().listenerManager.addListener(SicMidCombatAdder2())
        }else {
            log?.info("NOT ADDING THE LISTINER")
        }
        if (fleet != Global.getSector().playerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }


        var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = SCUtils.getFleetData(fleet)

        var skills = data.getAllActiveSkillsPlugins();//SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
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
        /*var member = ship?.mutableStats?.fleetMember ?: return
        var fleet = member.fleetData?.fleet ?: return

        if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }*/

        //var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = getFleetData(ship) ?: return;//SCUtils.getFleetData(fleet)

        var skills = data.getAllActiveSkillsPlugins();//SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
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

        //var member = ship?.fleetMember ?: return
        //var fleet = member.fleetData?.fleet ?: return

        /*if (!fleet.isPlayerFleet && Global.getSector().playerFleet?.fleetData?.membersListCopy?.contains(member) == true) {
            //Fix for battles where you join an ally, as those set the members fleet to theirs.
            fleet = Global.getSector().playerFleet
        }*/

        //var fleetData = fleet.fleetData ?: return //Have to do this, as during deserialisation fleetData can be null, causing save corruptions
        var data = getFleetData(ship) ?: return;//SCUtils.getFleetData(fleet)

        var skills = data.getAllActiveSkillsPlugins();//SCUtils.getFleetData(fleet).getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCombat(data, ship, amount)
        }

        if (data.isPlayer) {
            PlayerLevelEffects.advanceInCombat(data, ship, amount)
        }
    }
}