package second_in_command.skills.strikecraft.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.IntervalUtil
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import second_in_command.misc.getAndLoadSprite
import java.awt.Color
import java.util.LinkedHashMap

class SynchronisedSkillScript : BaseEveryFrameCombatPlugin() {

    var sprite = Global.getSettings().getAndLoadSprite("graphics/ui/sc_ship_marker.png")

    var originalShip: ShipAPI? = null
    var selectedShip: ShipAPI? = null
    var lastSelectedShip: ShipAPI? = null

    var rotation = MathUtils.getRandomNumberInRange(0f, 90f)
    var alpha = 0f

    var replenishedShips = ArrayList<ShipAPI>()
    var deplenishedShips = ArrayList<ShipAPI>()

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {

        if (!Global.getCombatEngine().isPaused) {
            rotation -= 10f * amount
            if (rotation >= 360f) {
                rotation = 0f
            }
        }

        //Switch to original on ship death
        var playership = Global.getCombatEngine().playerShip
        if (playership != null && !playership.isAlive) {


            var fighters = Global.getCombatEngine().ships.filter { it.isFighter && it.owner == 0 && it != playership && it.isAlive && MathUtils.getDistance(it, playership) <= 1600 }.sortedBy { MathUtils.getDistance(it, playership) }

            //Swap to nearest fighter, or original ship if theres none
            if (fighters.isNotEmpty() && playership.isFighter) {
                var swap = fighters.first()
                switchShip(playership, swap)
            } else {
                if (originalShip != null && playership != originalShip) {
                    switchShip(playership, originalShip!!)
                }
            }

        }

        applyEffectsToNearbyShips()

        if (playership != null && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {

            var shipsNearMouse = getValidShips()

            var any = false
            var closest = 10000000f
            for (ship in shipsNearMouse) {

                var distance = MathUtils.getDistance(playership.mouseTarget, ship.shieldCenterEvenIfNoShield)
                if (distance <= ship.shieldRadiusEvenIfNoShield * 2.6f) {
                    if (distance <= closest) {
                        closest = distance
                        selectedShip = ship
                        lastSelectedShip = ship
                        any = true
                    }
                }
            }

            if (!any) {
                selectedShip = null
            }
        }

        if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            selectedShip = null
        }

        if (selectedShip != null) {
            alpha += 4f * amount
        }
        else {
            alpha -= 4f * amount
        }
        alpha = alpha.coerceIn(0f, 1f)

    }

    fun applyEffectsToNearbyShips() {
        var playership = Global.getCombatEngine().playerShip



        if (playership != null && playership.isFighter) {
            var iter = Global.getCombatEngine().shipGrid.getCheckIterator(playership.location, 800f, 800f)
            for (obj in iter) {
                if (obj is ShipAPI) {
                    if (obj.isFighter && obj.owner == playership.owner && obj != playership) {

                        if (MathUtils.getDistance(obj, playership) <= 600) {
                            var listener = obj.getListeners(NearbyShipBuffListener::class.java).firstOrNull()
                            listener?.duration = 0.1f

                            if (listener == null) {
                                obj.addListener(NearbyShipBuffListener(obj))
                            }
                        }


                    }
                }
            }
        }
    }

    fun getValidShips() : List<ShipAPI> {

        var list = mutableListOf<ShipAPI>()

        var playership = Global.getCombatEngine().playerShip ?: return list
        var mouseLoc = playership.mouseTarget ?: return list
        var shipsNearMouse = Global.getCombatEngine().shipGrid.getCheckIterator(mouseLoc, 2000f, 2000f).iterator()

        var any = false
        var closest = 10000000f
        for (shipObj in shipsNearMouse) {
            var ship = shipObj as ShipAPI

            if (ship.owner != 0) continue
            if (!ship.isFighter) continue
            if (ship == playership) continue

            list.add(ship)
        }

        return list
    }



    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {
        super.processInputPreCoreControls(amount, events)

        var playership = Global.getCombatEngine().playerShip

        for (event in events!!) {
            if (!event.isConsumed) {
                if (event.isMouseDownEvent && event.isRMBDownEvent) {
                    if (selectedShip != null && playership != null) {

                        switchShip(playership, selectedShip!!)

                        event.consume()
                        break
                    }
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && originalShip != null && playership != null && playership != originalShip && selectedShip == null) {
                        switchShip(playership, originalShip!!)

                        event.consume()
                        break
                    }
                }
            }
        }
    }

    fun switchShip(current: ShipAPI, new: ShipAPI) {


        if (!current.isFighter) {
            originalShip = current;
        }

        if (new.isFighter) {
            var newStats = new.mutableStats

            if (!replenishedShips.contains(new)) {
                replenishedShips.add(new)
                for (weapon in new.allWeapons) {
                    weapon.ammo += weapon.spec.maxAmmo
                }
            }

            newStats.shieldDamageTakenMult.modifyMult("sc_synchronised", 0.75f)
            newStats.hullDamageTakenMult.modifyMult("sc_synchronised", 0.75f)
            newStats.armorDamageTakenMult.modifyMult("sc_synchronised", 0.75f)

            newStats.ballisticWeaponDamageMult.modifyMult("sc_synchronised", 1.30f)
            newStats.energyWeaponDamageMult.modifyMult("sc_synchronised", 1.30f)

            newStats.ballisticRoFMult.modifyMult("sc_synchronised", 1.5f)
            newStats.energyRoFMult.modifyMult("sc_synchronised", 1.5f)
            newStats.missileRoFMult.modifyMult("sc_synchronised", 1.5f)

            newStats.ballisticAmmoRegenMult.modifyMult("sc_synchronised", 1.5f)
            newStats.energyAmmoRegenMult.modifyMult("sc_synchronised", 1.5f)
            newStats.missileAmmoRegenMult.modifyMult("sc_synchronised", 1.5f)

            newStats.ballisticWeaponFluxCostMod.modifyMult("sc_synchronised", 0.5f)
            newStats.energyWeaponFluxCostMod.modifyMult("sc_synchronised", 0.5f)
            newStats.missileWeaponFluxCostMod.modifyMult("sc_synchronised", 0.5f)
        }

        var oldStats = current.mutableStats

        //Remove Ammo on leave
        if (current.isFighter) {
            if (replenishedShips.contains(current) && !deplenishedShips.contains(current)) {
                deplenishedShips.add(new)
                for (weapon in current.allWeapons) {
                    /*weapon.ammo -= weapon.spec.maxAmmo
                    weapon.ammo = MathUtils.clamp(weapon.ammo, 0, Int.MAX_VALUE) //Make sure it doesn't get below 0*/
                    weapon.ammo = MathUtils.clamp(weapon.ammo, 0, weapon.maxAmmo)
                }
            }
        }

        oldStats.shieldDamageTakenMult.unmodify("sc_synchronised")
        oldStats.hullDamageTakenMult.unmodify("sc_synchronised")
        oldStats.armorDamageTakenMult.unmodify("sc_synchronised")

        oldStats.ballisticWeaponDamageMult.unmodify("sc_synchronised")
        oldStats.energyWeaponDamageMult.unmodify("sc_synchronised")

        oldStats.ballisticRoFMult.unmodify("sc_synchronised")
        oldStats.energyRoFMult.unmodify("sc_synchronised")
        oldStats.missileRoFMult.unmodify("sc_synchronised")

        oldStats.ballisticAmmoRegenMult.unmodify("sc_synchronised")
        oldStats.energyAmmoRegenMult.unmodify("sc_synchronised")
        oldStats.missileAmmoRegenMult.unmodify("sc_synchronised")

        oldStats.ballisticWeaponFluxCostMod.unmodify("sc_synchronised")
        oldStats.energyWeaponFluxCostMod.unmodify("sc_synchronised")
        oldStats.missileWeaponFluxCostMod.unmodify("sc_synchronised")





        saveControlState(current)

        Global.getSoundPlayer().playUISound("ui_neural_transfer_complete", 1f, 1f)
        Global.getCombatEngine().combatUI.reFanOutShipInfo()

        Global.getCombatEngine().setPlayerShipExternal(new)

        restoreControlState(new)
        new.giveCommand(ShipCommand.SELECT_GROUP, null, 0)

        Global.getCombatEngine().addPlugin(TemporarySlowdown(3f, 0.25f))

        selectedShip = null

        if (new.isFighter) {
            applyEffectsToNearbyShips()
        }
    }



    fun saveControlState(ship: ShipAPI) {

        var controllState = ShipControllState()
        for (group in ship.weaponGroupsCopy) {
            controllState!!.autofiring.put(group, group.isAutofiring)
        }
        controllState!!.selected = ship.selectedGroupAPI

        ship.setCustomData("sc_synchronised_state", controllState)
    }

    fun restoreControlState(ship: ShipAPI) {
        var controllState = ship.customData.get("sc_synchronised_state") as ShipControllState? ?: return

        for (group in ship.weaponGroupsCopy) {
            var auto: Boolean? = controllState!!.autofiring.get(group)
            if (auto == null) auto = false
            if (auto) {
                group.toggleOn()
            } else {
                group.toggleOff()
            }
        }
        val index = ship.weaponGroupsCopy.indexOf(controllState!!.selected)
        if (index > 0) {
            ship.giveCommand(ShipCommand.SELECT_GROUP, null, index)
        }
    }

    override fun renderInWorldCoords(viewport: ViewportAPI?) {
        super.renderInWorldCoords(viewport)

        if (lastSelectedShip != null) {
            sprite.color = Color(0, 230, 150)
            sprite.alphaMult = alpha * 0.5f

            var degrees = rotation
            for (i in 0 until 3) {

                var maxRadius = 600f
                var radius = lastSelectedShip!!.collisionRadius

                var level = radius / maxRadius
                level = level.coerceIn(0.15f, 1f)

                var loc = MathUtils.getPointOnCircumference(lastSelectedShip!!.shieldCenterEvenIfNoShield, lastSelectedShip!!.shieldRadiusEvenIfNoShield * 2f, degrees)

                //sprite.setSize(35f, 96f)
                //sprite.setSize(140f * level, 383f * level)
                sprite.setSize(105f * level, 287.25f * level)
                //sprite.setSize(width * level, height * level)
                sprite.angle = degrees
                sprite.renderAtCenter(loc.x, loc.y)

                degrees += 120f
            }
        }

    }



    class ShipControllState {
        var autofiring: MutableMap<WeaponGroupAPI, Boolean> = LinkedHashMap()
        var selected: WeaponGroupAPI? = null
    }

}

class NearbyShipBuffListener(var ship: ShipAPI) : AdvanceableListener {

    var duration = 0.1f

    init {
        var stats = ship.mutableStats

        stats.shieldDamageTakenMult.modifyMult("sc_synchronised_nearby", 0.915f)
        stats.hullDamageTakenMult.modifyMult("sc_synchronised_nearby", 0.915f)
        stats.armorDamageTakenMult.modifyMult("sc_synchronised_nearby", 0.915f)

        stats.ballisticWeaponDamageMult.modifyMult("sc_synchronised_nearby", 1.1f)
        stats.energyWeaponDamageMult.modifyMult("sc_synchronised_nearby", 1.1f)

        stats.ballisticRoFMult.modifyMult("sc_synchronised_nearby", 1.165f)
        stats.energyRoFMult.modifyMult("sc_synchronised_nearby", 1.165f)
        stats.missileRoFMult.modifyMult("sc_synchronised_nearby", 1.165f)

        stats.ballisticAmmoRegenMult.modifyMult("sc_synchronised_nearby", 1.165f)
        stats.energyAmmoRegenMult.modifyMult("sc_synchronised_nearby", 1.165f)
        stats.missileAmmoRegenMult.modifyMult("sc_synchronised_nearby", 1.165f)

        stats.ballisticWeaponFluxCostMod.modifyMult("sc_synchronised_nearby", 0.835f)
        stats.energyWeaponFluxCostMod.modifyMult("sc_synchronised_nearby", 0.835f)
        stats.missileWeaponFluxCostMod.modifyMult("sc_synchronised_nearby", 0.835f)
    }

    override fun advance(amount: Float) {
        duration -= 1 * amount

        ship.setJitterUnder(this, Color(0, 230, 150), 0.15f, 10, 2f, 7f)

        if (duration <= 0) {

            var stats = ship.mutableStats

            stats.shieldDamageTakenMult.unmodify("sc_synchronised_nearby")
            stats.hullDamageTakenMult.unmodify("sc_synchronised_nearby")
            stats.armorDamageTakenMult.unmodify("sc_synchronised_nearby")

            stats.ballisticWeaponDamageMult.unmodify("sc_synchronised_nearby")
            stats.energyWeaponDamageMult.unmodify("sc_synchronised_nearby")

            stats.ballisticRoFMult.unmodify("sc_synchronised_nearby")
            stats.energyRoFMult.unmodify("sc_synchronised_nearby")
            stats.missileRoFMult.unmodify("sc_synchronised_nearby")

            stats.ballisticAmmoRegenMult.unmodify("sc_synchronised_nearby")
            stats.energyAmmoRegenMult.unmodify("sc_synchronised_nearby")
            stats.missileAmmoRegenMult.unmodify("sc_synchronised_nearby")

            stats.ballisticWeaponFluxCostMod.unmodify("sc_synchronised_nearby")
            stats.energyWeaponFluxCostMod.unmodify("sc_synchronised_nearby")
            stats.missileWeaponFluxCostMod.unmodify("sc_synchronised_nearby")

            ship.removeListener(this)
        }
    }
}

