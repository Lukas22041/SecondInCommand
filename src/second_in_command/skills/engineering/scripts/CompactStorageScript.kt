package second_in_command.skills.engineering.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.PlayerMarketTransaction
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.impl.campaign.graid.ShipWeaponsGroundRaidObjectivePluginImpl
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.campaign.ui.trade.CargoItemStack
import second_in_command.SCUtils
import second_in_command.misc.ReflectionUtils

class CompactStorageScript : EveryFrameScript, ColonyInteractionListener {

    var interval = IntervalUtil(0.075f, 0.1f)
    //var inactiveInterval = IntervalUtil(0.15f, 0.2f)

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {

        interval.advance(amount)
        //inactiveInterval.advance(amount)

        if (interval.intervalElapsed()) {
            modifyCargo(Global.getSector().playerFleet.cargo)
        }

       /* if (inactiveInterval.intervalElapsed()) {
            modifyCargo(Global.getSector().playerFleet.cargo)
        }*/


    }

    fun modifyCargo(cargo: CargoAPI) {

        var data = SCUtils.getPlayerData() ?: return

        //If Skill is active
        if (data.isSkillActive("sc_engineering_compact_storage"))
        {
            var changedSize = false
            for (stack in cargo.stacksCopy) {
                var obfStack = stack as CargoItemStack
                if (obfStack.type == CargoAPI.CargoItemType.WEAPONS) {
                    var spec = Global.getSettings().getWeaponSpec(obfStack.data as String)
                    var space = when (spec.size) {
                        WeaponAPI.WeaponSize.SMALL -> 1f
                        WeaponAPI.WeaponSize.MEDIUM -> 1f
                        WeaponAPI.WeaponSize.LARGE -> 2f
                        else -> 1f
                    }

                    //Only use reflection if this stack is unmodified
                    if (space != obfStack.cargoSpacePerUnit) {
                        changedSize = true
                        ReflectionUtils.set("cargoSpacePerUnit", obfStack, space)
                    }
                }
            }
            //Only Update space used if any stack changed.
            if (changedSize) {
                Global.getSector().playerFleet.cargo.updateSpaceUsed()
            }
        }
        //If Skill is inactive
        else
        {
            var changedSize = false
            for (stack in cargo.stacksCopy) {
                var obfStack = stack as CargoItemStack
                if (obfStack.type == CargoAPI.CargoItemType.WEAPONS) {
                    var spec = Global.getSettings().getWeaponSpec(obfStack.data as String)
                    var space = when (spec.size) {
                        WeaponAPI.WeaponSize.SMALL -> ShipWeaponsGroundRaidObjectivePluginImpl.CARGO_SPACE_PER_SMALL
                        WeaponAPI.WeaponSize.MEDIUM -> ShipWeaponsGroundRaidObjectivePluginImpl.CARGO_SPACE_PER_MEDIUM
                        WeaponAPI.WeaponSize.LARGE -> ShipWeaponsGroundRaidObjectivePluginImpl.CARGO_SPACE_PER_LARGE
                        else -> ShipWeaponsGroundRaidObjectivePluginImpl.CARGO_SPACE_PER_SMALL
                    }

                    //Only use reflection if this stack is unmodified
                    if (space != obfStack.cargoSpacePerUnit) {
                        changedSize = true
                        ReflectionUtils.set("cargoSpacePerUnit", obfStack, space)
                    }
                }
            }
            //Only Update space used if any stack changed.
            if (changedSize) {
                Global.getSector().playerFleet.cargo.updateSpaceUsed()
            }
        }
    }

    override fun reportPlayerOpenedMarket(market: MarketAPI?) {

    }

    override fun reportPlayerClosedMarket(market: MarketAPI?) {

    }

    override fun reportPlayerOpenedMarketAndCargoUpdated(market: MarketAPI?) {
       /* if (market == null) return
        for (submarket in market.submarketsCopy) {
            var cargo = submarket.cargo ?: continue
            modifyCargo(cargo)
        }*/
    }

    override fun reportPlayerMarketTransaction(transaction: PlayerMarketTransaction?) {
        var test = ""
    }

}