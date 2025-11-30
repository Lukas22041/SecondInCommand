package second_in_command.skills.scavenging.abilities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility
import com.fs.starfarer.api.loading.AbilitySpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import second_in_command.SCUtils
import second_in_command.skills.scavenging.entities.ScrapforgeDeployable

class CraftSuppliesAbility : BaseDurationAbility() {

    companion object {
        var craftForDays = 14
        var maximum = 250f
    }

    fun getScrapCost() : Float {
        return 25f
    }

    fun getSuppliesToCraft() : Float {

        var baseCost = 0f
        var deployCost = 0f
        for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
            baseCost += member.stats.suppliesPerMonth.modifiedValue / 30f
            deployCost += member.deploymentCostSupplies * 0.3f
        }

        var toCraft = deployCost + (baseCost * craftForDays)
        toCraft = MathUtils.clamp(toCraft, 0f, maximum)
        return toCraft
    }

    override fun activateImpl() {
        var data = SCUtils.getFleetData(this.fleet)
        data.scrapManager.adjustScrap(-getScrapCost())
        this.fleet.cargo.addSupplies(getSuppliesToCraft())
    }

    override fun isUsable(): Boolean {
        return super.isUsable() && SCUtils.getFleetData(this.fleet).isSkillActive("sc_scavenging_field_recycling") && SCUtils.getFleetData(this.fleet).scrapManager.getCurrentScrap() >= getScrapCost()
    }

    override fun hasTooltip(): Boolean {
        return true
    }


    override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean) {

        addAbilityTooltip(tooltip, spec)

        var data = SCUtils.getPlayerData()
        var notEnoughScrap = data.scrapManager.getCurrentScrap() < getScrapCost()
        if (notEnoughScrap) {
            tooltip.addSpacer(10f)
            tooltip.addPara("You do not have enough Scrap to use the ability.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }

        data.scrapManager.setScrapConsumptionThisFrame(getScrapCost())
    }

    fun addAbilityTooltip(tooltip: TooltipMakerAPI, spec: AbilitySpecAPI) {
        tooltip.addTitle(spec.name)
        tooltip.addSpacer(10f)
        tooltip.addPara("Crafts supplies based on how much your fleet requires to sustain itself for the next ${craftForDays.toInt()} days. Additionally 30%% of the costs of a single deployment are included. " +
                "The total amount of supplies is limited to ${maximum.toInt()} supplies per craft. At the current rate, this would produce ${getSuppliesToCraft().toInt()} supplies." +
                "\n\n" +
                "Requires ${getScrapCost().toInt()}%% Scrap to use.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${craftForDays.toInt()}", "30%", "${maximum.toInt()}", "${getSuppliesToCraft().toInt()}", "${getScrapCost().toInt()}%")
    }

    override fun applyEffect(amount: Float, level: Float) {

    }

    override fun deactivateImpl() {

    }

    override fun cleanupImpl() {

    }

    /*var layers = EnumSet.of(CampaignEngineLayers.ABOVE)
    override fun getActiveLayers(): EnumSet<CampaignEngineLayers> {
        return layers
    }*/


}