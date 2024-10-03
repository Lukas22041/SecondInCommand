package second_in_command.interactions.rules

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CharacterDataAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.characters.CharacterCreationData
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest
import com.fs.starfarer.api.util.Misc

class SCStoreSkillpoints : BaseCommandPlugin() {

    override fun execute(ruleId: String?, dialog: InteractionDialogAPI?,  params: MutableList<Misc.Token>?, memoryMap: MutableMap<String, MemoryAPI>?): Boolean {

        var recursive = params!!.get(0).getString(memoryMap)

        var data = memoryMap!!.get(MemKeys.LOCAL)!!.get("\$characterData") as CharacterCreationData
        var points = data.characterData.person.stats.points
        data.characterData.person.stats.points = 0

        data.addScript {
            //data.characterData.person.stats.points = points
            data.characterData.person.stats.points += points
        }


        if (recursive == "true") {
            memoryMap.get(MemKeys.LOCAL)!!.set("\$sc_do_not_trigger_again", true)
            FireBest.fire(null, dialog, memoryMap, "NewGameOptionSelected")
        }

        return true
    }

}