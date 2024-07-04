package second_in_command

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import second_in_command.scripts.SkillPanelReplacerScript
import second_in_command.specs.SCSpecStore

class SCModPlugin : BaseModPlugin() {

    override fun onApplicationLoad() {
        SCSpecStore.loadAptitudeSpecsFromCSV()
        SCSpecStore.loadSkillSpecsFromCSV()
    }

    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().addTransientScript(SkillPanelReplacerScript())

        var data = Global.getSector().characterData
        var test = ""
    }
}