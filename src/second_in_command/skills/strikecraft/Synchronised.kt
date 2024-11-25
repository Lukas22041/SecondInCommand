package second_in_command.skills.strikecraft

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class Synchronised : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "player"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("You gain the ability to swap control to fighters deployed from your fleet", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - To switch to a fighter, hold CTRL & press right-click while hovering over a fighter", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "CTRL", "right-click")
        tooltip.addPara("   - Pressing CTRL + right-click while not hovering over another fighter will return you to your main ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "CTRL", "right-click")
        tooltip.addPara("   - Selecting the leader of a wing will make the rest of it follow you", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")

        tooltip.addSpacer(10f)

        tooltip.addPara("Once per fighter, switching control to it restores all of its ammunition, including missiles", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The restored ammunition can go over the fighters maximum capacity", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Ammunition over the maximum is removed when the player stops controlling the fighter", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The restored amount is based on the weapons base capacity", 0f, Misc.getTextColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("Fighters piloted by yourself will not receive your skills, but receive several bonuses", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The fighter gains 25%% damage resistance", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "25%")
        tooltip.addPara("   - The fighter gains 30%% increased non-missile damage ", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "30%")
        tooltip.addPara("   - The fighter gains 50%% increased fire rate", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "50%")
        tooltip.addPara("   - The fighter gains 50%% decreased weapon flux usage", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "50%")
        tooltip.addPara("   - Allied fighters in a range of 600 units receive 1/3 of the bonuses as well", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "600", "1/3")

        tooltip.addSpacer(10f)



    }

    override fun advanceInCombat(data: SCData?, ship: ShipAPI?, amount: Float?) {
        super.advanceInCombat(data, ship, amount)

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {


    }

}