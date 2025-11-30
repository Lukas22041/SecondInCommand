package second_in_command.skills.scavenging

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.skills.scavenging.abilities.CraftFuelAbility
import second_in_command.skills.scavenging.abilities.CraftSuppliesAbility
import second_in_command.specs.SCBaseSkillPlugin

class FieldRecycling : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Unlocks and enables the following abilities", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        var supplyAbilitySpec = Global.getSettings().getAbilitySpec("sc_craft_supplies")
        var supplyImg = tooltip.beginImageWithText(supplyAbilitySpec.iconName, 48f)
        var supplyAbility = CraftSuppliesAbility()
        supplyAbility.addAbilityTooltip(supplyImg, supplyAbilitySpec)
        tooltip.addImageWithText(0f)

        tooltip.addSpacer(20f)

        var fuelAbilitySpec = Global.getSettings().getAbilitySpec("sc_craft_fuel")
        var disruptorImage = tooltip.beginImageWithText(fuelAbilitySpec.iconName, 48f)
        var fuelAbility = CraftFuelAbility()
        fuelAbility.addAbilityTooltip(disruptorImage, fuelAbilitySpec)
        tooltip.addImageWithText(0f)

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {
        if (!data.fleet.hasAbility("sc_craft_supplies")) {
            data.fleet.addAbility("sc_craft_supplies")
            if (data.isPlayer) {
                Global.getSector().characterData.addAbility("sc_craft_supplies")
            }
        }

        if (!data.fleet.hasAbility("sc_craft_fuel")) {
            data.fleet.addAbility("sc_craft_fuel")
            if (data.isPlayer) {
                Global.getSector().characterData.addAbility("sc_craft_fuel")
            }
        }
    }

    override fun onDeactivation(data: SCData) {

    }

}