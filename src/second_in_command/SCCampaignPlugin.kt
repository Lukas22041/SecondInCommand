package second_in_command

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.PluginPick
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.BaseCampaignPlugin
import com.fs.starfarer.api.campaign.CampaignPlugin
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.BaseAICoreOfficerPluginImpl
import java.util.*

class SCCampaignPlugin : BaseCampaignPlugin() {


    override fun isTransient(): Boolean {
        return true
    }

    override fun pickAICoreOfficerPlugin(commodityId: String?): PluginPick<AICoreOfficerPlugin>? {

        if (commodityId == "sc_neural_junction" && SCUtils.getPlayerData().isSkillActive("sc_automated_neural_junction")) {
            return PluginPick<AICoreOfficerPlugin>(object : BaseAICoreOfficerPluginImpl() {
                override fun createPerson(aiCoreId: String?, factionId: String?, random: Random?): PersonAPI {
                    var player = Global.getSector().playerPerson
                    player.memoryWithoutUpdate.set(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT, 1f)
                    return player
                }
            }, CampaignPlugin.PickPriority.HIGHEST)
        }

        return null
    }
}