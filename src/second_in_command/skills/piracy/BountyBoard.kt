package second_in_command.skills.piracy

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BattleAPI
import com.fs.starfarer.api.campaign.CampaignEventListener
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.campaign.listeners.FleetEventListener
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin
import java.awt.Color

class BountyBoard : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        var creditsString = Misc.getDGSCredits(600f)
        tooltip.addPara("You receive $creditsString credits per opposing ship destroyed or disabled in combat", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This is multiplied by 1x/2x/3x/10x based on the hullsize of each destroyed ship", 0f, Misc.getTextColor(), Misc.getHighlightColor(),
        "1x", "2x", "3x", "10x")
        tooltip.addPara("   - The payment is halved for civilian targets", 0f, Misc.getTextColor(), Misc.getHighlightColor(),
            "halved")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun onActivation(data: SCData) {
        if (data.isPlayer) {
            if (!Global.getSector().listenerManager.hasListenerOfClass(BountyBoardSkillListener::class.java)) {
                Global.getSector().listenerManager.addListener(BountyBoardSkillListener())
            }
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.isPlayer) {
            if (Global.getSector().listenerManager.hasListenerOfClass(BountyBoardSkillListener::class.java)) {
                Global.getSector().listenerManager.removeListenerOfClass(BountyBoardSkillListener::class.java)
            }
        }
    }

}

class BountyBoardIntel(var payment: Float) : BaseIntelPlugin() {

    init {
        Global.getSector().addScript(this)
        endAfterDelay(14f)
    }

    override fun notifyEnded() {
        Global.getSector().removeScript(this)
    }

    override fun getName(): String {
        return "Skill - Privateering"
    }

    override fun getIcon(): String {
        return "graphics/secondInCommand/piracy/bounty_board.png"
    }

    override fun hasSmallDescription(): Boolean {
        return false
    }

    override fun addBulletPoints(info: TooltipMakerAPI?, mode: IntelInfoPlugin.ListInfoMode?, isUpdate: Boolean, tc: Color?, initPad: Float) {

        var paymentString = Misc.getDGSCredits(payment)
        info!!.addPara("Gained $paymentString credits from the recent encounter.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$paymentString")
    }

    override fun getTitleColor(mode: IntelInfoPlugin.ListInfoMode?): Color {
        return Misc.getBasePlayerColor()
    }

    override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        var tags = super.getIntelTags(map)
        tags.add("Skills")
        return tags
    }
}

class BountyBoardSkillListener() : FleetEventListener {

    var base = 600f

    override fun reportFleetDespawnedToListener(fleet: CampaignFleetAPI?, reason: CampaignEventListener.FleetDespawnReason?,  param: Any?) {
    }

    override fun reportBattleOccurred(fleet: CampaignFleetAPI?, primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {
        if (!battle!!.isPlayerInvolved) return


        var payment = 0f

        for (otherFleet in battle.nonPlayerSideSnapshot) {
            var bounty = 0f
            for (loss in Misc.getSnapshotMembersLost(otherFleet)) {
                //val mult = Misc.getSizeNum(loss.hullSpec.hullSize)
                val mult = when(loss.baseOrModSpec().hullSize) {
                    ShipAPI.HullSize.CAPITAL_SHIP -> 10f
                    ShipAPI.HullSize.CRUISER -> 3f
                    ShipAPI.HullSize.DESTROYER -> 2f
                    ShipAPI.HullSize.FRIGATE -> 1f
                    else -> 1f
                }

                var reward = mult * base
                if (loss.isCivilian) reward *= 0.5f //Half for civilian targets

                bounty += reward
            }
            payment += (bounty * battle.playerInvolvementFraction).toInt()
        }

        Global.getSector().playerFleet.cargo.credits.add(payment)

        var intel = BountyBoardIntel(payment)

        Global.getSector().intelManager.addIntel(intel)

        /*Global.getSector().campaignUI.addMessage(object : BaseIntelPlugin() {
            override fun getName(): String {
                return "Skill - Privateering"
            }

            override fun getIcon(): String {
                return "graphics/secondInCommand/piracy/bounty_board.png"
            }

            override fun addBulletPoints(info: TooltipMakerAPI?, mode: IntelInfoPlugin.ListInfoMode?, isUpdate: Boolean, tc: Color?, initPad: Float) {

                var paymentString = Misc.getDGSCredits(payment)
                info!!.addPara("Gained $paymentString credits from the recent encounter.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "$paymentString")

            }
        })*/
    }

}
