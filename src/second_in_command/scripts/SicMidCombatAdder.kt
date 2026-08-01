package second_in_command.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.listeners.FleetMemberDeploymentListener
import second_in_command.SCData
import second_in_command.hullmods.SCControllerHullmod
import kotlin.collections.contains
import second_in_command.SCUtils.noSkillTagHullmodID
import second_in_command.SCUtils.secOverrideKey

class SicMidCombatAdder : FleetMemberDeploymentListener{
    //var addModules = Global.getSettings().getBoolean("sc_applyToModules");
    //var addFighter = false;
    init{
        var originalOwnerMap: HashMap<Int?, SCData?> = HashMap<Int?, SCData?>()
        var fleetCommanderMap: HashMap<String?, SCData?> = HashMap<String?, SCData?>()
        val engine = Global.getCombatEngine()
        //this loop is here because this plugin is added after the first ship is created. basicly gets all 'on spawned' ships and adds them to relevant listeners.
        //notice: this does not work on the first ship added in simulations. That ship is never added with this method. If you want that, make it so this plugin is delayed by a 10nth of a second. That should work.
        for (ship in engine.ships) {
            if (!isShip(ship)) continue;
            if (alreadyReady(ship)){
                var data: SCData? = SCControllerHullmod.getFleetData(ship);
                if (data == null) continue
                originalOwnerMap.put(ship.originalOwner,data);
                if (ship.fleetCommander != null) fleetCommanderMap.put(ship.fleetCommander.id,data);
                //addModules(ship,data);
                continue
            }
        }
        Global.getCombatEngine().customData.put("SiC_OriginalOwner_StoredDataMap",originalOwnerMap);
        Global.getCombatEngine().customData.put("SiC_FleetCommander_StoredDataMap",fleetCommanderMap);
    }
    override fun reportFleetMemberDeployed(member: DeployedFleetMemberAPI?) {
        var ship = member?.ship;
        //if (ship != null && ship.hullSize == ShipAPI.HullSize.FIGHTER){
        //    if (!addFighter) return;
        //    processFighter(ship);
        //    return;
        //}
        if (ship == null || !isShip(ship)) return
        if (alreadyReady(ship)){
            var originalOwnerMap: HashMap<Int?, SCData?> = Global.getCombatEngine().customData.get("SiC_OriginalOwner_StoredDataMap") as HashMap<Int?, SCData?>;
            var fleetCommanderMap: HashMap<String?, SCData?> = Global.getCombatEngine().customData.get("SiC_FleetCommander_StoredDataMap") as HashMap<String?, SCData?>;
            var data: SCData? = SCControllerHullmod.getFleetData(ship);
            if (data == null) return
            originalOwnerMap.put(ship.originalOwner,data);
            if (ship.fleetCommander != null) fleetCommanderMap.put(ship.fleetCommander.id,data);
            Global.getCombatEngine().customData.put("SiC_OriginalOwner_StoredDataMap",originalOwnerMap);
            Global.getCombatEngine().customData.put("SiC_FleetCommander_StoredDataMap",fleetCommanderMap);
            //addModules(ship,data);
        }else if (isValidShipToConvert(ship)){
            var originalOwnerMap: HashMap<Int?, SCData?> = Global.getCombatEngine().customData.get("SiC_OriginalOwner_StoredDataMap") as HashMap<Int?, SCData?>;
            var fleetCommanderMap: HashMap<String?, SCData?> = Global.getCombatEngine().customData.get("SiC_FleetCommander_StoredDataMap") as HashMap<String?, SCData?>;
            val force = ship.originalOwner

            var data: SCData? = null;
            if (convertWithFleetCommander(ship)){
                if (ship.fleetCommander != null && fleetCommanderMap.containsKey(ship.fleetCommander.id)) data = fleetCommanderMap.get(ship.fleetCommander.id)
            }else{
                //note: if this somehow does not work, the next best thing is to get the closest ship with fleet data.
                if (originalOwnerMap.containsKey(force)) data = originalOwnerMap.get(force);
            }
            if (data == null) return
            refitShip(ship,data);
            //addModules(ship,data);
        }
    }
    private fun processFighter(a : ShipAPI){
        //todo: this is for adding fighters that are not spawned from fighter bays (attack swarms, whatever omega does when it dies, HMI mess, secret of teh frontier 'fighter spawning weapon', so on)
        //      how it would work, is I would get the closest friendly ship with SCData (by looking at all spawned ships), and then running the 'for skill' code in 'applyEffectsToFighterSpawnedByShip'.
        //      This would work. But I am so fucking done right now. I am keeping the logs were in case me, or some
        //val log: Logger? = Global.getLogger(SCControllerHullmod::class.java)
        //log?.info("already processed: "+a.hasTag(SCControllerHullmod.Companion.noSkillTagHullmodID));
        //log?.info("got fighter as: "+a.hullSpec.hullId+", mothership: "+a.wing.sourceShip)
    }
    private fun isValidShipToConvert(a: ShipAPI) : Boolean{
        if (a.variant.hasHullMod(noSkillTagHullmodID)) return false;
        if (a.variant.hasTag(noSkillTagHullmodID)) return false;
        if (a.hullSpec.hasTag(noSkillTagHullmodID)) return false;
        if (a.hasTag(noSkillTagHullmodID)) return false;
        return true;
    }
    private fun convertWithFleetCommander(a: ShipAPI) : Boolean{
        if (a.fleetMember != null && a.fleetMember.fleetData != null) return true;
        return false;
    }
    private fun alreadyReady(shipAPI: ShipAPI) : Boolean{
        return shipAPI.variant.hasHullMod("sc_skill_controller") && (shipAPI.customData.contains(secOverrideKey) || shipAPI.fleetMember.fleetData != null);
    }
    private fun isShip(shipAPI: ShipAPI) : Boolean{
        return !shipAPI.isHulk && shipAPI.isAlive && shipAPI.hullSize != ShipAPI.HullSize.FIGHTER && !shipAPI.isStationModule && shipAPI.parentStation == null
    }
    private fun refitShip(shipAPI: ShipAPI, data: SCData?){
        if (data == null) return
        SCControllerHullmod.addHullmodAfterShipCreation(shipAPI, data);
    }
    private fun addModules(shipAPI: ShipAPI, data: SCData?){
        //if (!addModules) return
        val childs = ArrayList<ShipAPI?>()
        childs.addAll(shipAPI.childModulesCopy)
        var b = 0;
        while (b != childs.size){
            var a = childs[b]
            var aLinks = a?.childModulesCopy;
            if (aLinks != null) {
                for (c in aLinks){
                    if (childs.contains(c)) continue
                    childs.add(c);
                }
            }
            if (a != null && !alreadyReady(a) && isValidShipToConvert(a)) refitShip(a,data);
            b++;
        }
    }
}
