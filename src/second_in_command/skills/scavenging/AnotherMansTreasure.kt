package second_in_command.skills.scavenging

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.addPara
import second_in_command.specs.SCBaseSkillPlugin

class AnotherMansTreasure : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        var scrapCount = 0

        tooltip.addPara("Salvaging abandoned stations, derelicts or salvaging after combat now also scavenges Scrap", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Scrap is a resource that does not take up inventory space, that can be used by other skills within the aptitude", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("It is displayed above the ability hotbar, tracked as a percentage of your maximum scrap storage (max 100%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("On average, different sources return the following amount of Scrap: ", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 10%%-40%% from post-battle salvage, based on battle size", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%", "40%")
        tooltip.addPara("   - 20%% from salvaging abandoned stations", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "20%")
        tooltip.addPara("   - 10%% from salvaging debris fields", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - 5%% from salvaging derelict ships", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "5%")

        tooltip.addSpacer(10f)

        tooltip.addPara("Scrap will remain within the fleets inventory even if no officer with the scavenging aptitude is active, but will then decay at a rate of 1%% per day. ", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "1%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {

    }

    override fun onDeactivation(data: SCData) {

    }

}