package second_in_command.skills.scavenging

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.skills.scavenging.abilities.CraftSuppliesAbility
import second_in_command.skills.scavenging.abilities.ReEvaluateAbility
import second_in_command.specs.SCBaseSkillPlugin

class ReEvaluate : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Unlocks and enables the following ability", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        var reevaluateAbilitySpec = Global.getSettings().getAbilitySpec("sc_re_evaluate")
        var reevaluateImg = tooltip.beginImageWithText(reevaluateAbilitySpec.iconName, 48f)
        var reeveluateAbility = ReEvaluateAbility()
        reeveluateAbility.addAbilityTooltip(reevaluateImg, reevaluateAbilitySpec)
        tooltip.addImageWithText(0f)

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {
        if (!data.fleet.hasAbility("sc_re_evaluate")) {
            data.fleet.addAbility("sc_re_evaluate")
            if (data.isPlayer) {
                Global.getSector().characterData.addAbility("sc_re_evaluate")
            }
        }
    }

    override fun onDeactivation(data: SCData) {

    }

}