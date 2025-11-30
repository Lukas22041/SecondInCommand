package second_in_command.skills.scavenging.abilities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility
import com.fs.starfarer.api.loading.AbilitySpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import second_in_command.SCUtils
import second_in_command.skills.scavenging.entities.ScrapforgeDeployable

class ScrapforgeDisruptorAbility : BaseDurationAbility() {

    fun getScrapCost() : Float {
        return 30f
    }

    override fun activateImpl() {
        var data = SCUtils.getFleetData(this.fleet)
        data.scrapManager.adjustScrap(-getScrapCost())

        var loc = Global.getSector().playerFleet.location
        var system = Global.getSector().playerFleet.starSystem

        var deployable = system.addCustomEntity("sc_scrapforge_deployable_${Misc.genUID()}", "Disruptor Relay", "sc_scrapforge_deployable", fleet.faction.id)
        deployable.location.set(Vector2f(loc))
        var plugin = deployable.customPlugin as ScrapforgeDeployable
        plugin.type = ScrapforgeDeployable.Type.DISRUPTOR
        plugin.timeToLife = 7f
    }

    override fun isUsable(): Boolean {
        return super.isUsable() && Global.getSector().playerFleet.starSystem != null && SCUtils.getFleetData(this.fleet).isSkillActive("sc_scavenging_scrapforge_constructs") && SCUtils.getFleetData(this.fleet).scrapManager.getCurrentScrap() >= getScrapCost()
    }

    override fun hasTooltip(): Boolean {
        return true
    }

    override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean) {

        addAbilityTooltip(tooltip, spec)

        var data = SCUtils.getPlayerData()
        var notEnoughScrap = data.scrapManager.getCurrentScrap() < getScrapCost()

        if (Global.getSector().playerFleet.starSystem == null) {
            tooltip.addSpacer(10f)
            tooltip.addPara("Can not be used in hyperspace.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }
        else if (notEnoughScrap) {
            tooltip.addSpacer(10f)
            tooltip.addPara("You do not have enough Scrap to use the ability.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())
        }

        data.scrapManager.setScrapConsumptionThisFrame(getScrapCost())
    }

    fun addAbilityTooltip(tooltip: TooltipMakerAPI, spec: AbilitySpecAPI) {
        tooltip.addTitle(spec.name)
        tooltip.addSpacer(10f)
        tooltip.addPara("Deploys a small disruptor array that lasts for 7 days. It emits waves of signals that throw off the sensors of other fleets within the system, making them chase what is not actually there. Does not distract fleets that are already chasing you. " +
                "\n\n" +
                "Requires ${getScrapCost().toInt()}%% Scrap to use.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "7", "${getScrapCost().toInt()}%")
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