package second_in_command.skills.scavenging.abilities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility
import com.fs.starfarer.api.loading.AbilitySpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils
import second_in_command.skills.scavenging.abilities.dialogs.ReEvaluateDialog

class ReEvaluateAbility : BaseDurationAbility() {

    fun getScrapCost() : Float {
        return 25f
    }



    override fun activateImpl() {
        if (!Global.getSector().campaignUI.isShowingDialog) {
            var data = SCUtils.getFleetData(this.fleet)
            data.scrapManager.adjustScrap(-getScrapCost())
            Global.getSector().campaignUI.showInteractionDialog(ReEvaluateDialog(), Global.getSector().playerFleet)
        }




    }

    override fun isUsable(): Boolean {
        return super.isUsable() && SCUtils.getFleetData(this.fleet).isSkillActive("sc_scavenging_reevaluate") && SCUtils.getFleetData(this.fleet).scrapManager.getCurrentScrap() >= getScrapCost()
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
        tooltip.addPara("Scavenge through your Scrap reserves to identify objects of value. Can result in basic supplies, weapons and blueprints. " +
                "Colony items can not be discovered within this process. The amount of basic supplies has slight scaling based on your fleets size. " +
                "\n\n" +
                "Requires ${getScrapCost().toInt()}%% Scrap to use.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "basic supplies", "weapons", "blueprints", "","${getScrapCost().toInt()}%")
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