package second_in_command

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import lunalib.lunaDebug.LunaDebug
import second_in_command.misc.VanillaSkillsUtil
import second_in_command.misc.baseOrModSpec
import second_in_command.misc.snippets.AddAllOfficersSnippet
import second_in_command.misc.snippets.AddXPToOfficersSnippet
import second_in_command.scripts.*
import second_in_command.specs.SCSpecStore

class SCModPlugin : BaseModPlugin() {

    override fun onApplicationLoad() {
        LunaDebug.addSnippet(AddAllOfficersSnippet())
        LunaDebug.addSnippet(AddXPToOfficersSnippet())

        SCSpecStore.loadAptitudeSpecsFromCSV()
        SCSpecStore.loadSkillSpecsFromCSV()
    }

    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().addTransientScript(SkillPanelReplacerScript())
        Global.getSector().addTransientScript(ControllerHullmodAdderScript())
        Global.getSector().addTransientScript(SkillAdvancerScript())
        Global.getSector().addTransientScript(SCNeuralJunctionScript())

        Global.getSector().addTransientListener(SCCampaignEventListener())

        Global.getSector().registerPlugin(SCCampaignPlugin())

        //Add Abilities that no longer have a skill
        if (!Global.getSector().characterData.abilities.contains(Abilities.TRANSVERSE_JUMP)) {
            Global.getSector().characterData.addAbility(Abilities.TRANSVERSE_JUMP)
        }

        if (!Global.getSector().characterData.abilities.contains(Abilities.GRAVITIC_SCAN)) {
            Global.getSector().characterData.addAbility(Abilities.GRAVITIC_SCAN)
        }
    }

    override fun onNewGame() {
        super.onNewGame()

        Global.getSector().listenerManager.addListener(ExecutiveOfficerCommAdder(), false)

    }
    override fun onNewGameAfterProcGen() {
        super.onNewGame()

        ExecutiveOfficerSalvageSpecialGenerator().generate()
    }

    override fun onNewGameAfterTimePass() {
        super.onNewGameAfterTimePass()

        var skills = SCUtils.getSCData().getAllActiveSkillsPlugins()

        for (skill in skills) {
            skill.onActivation()
        }
    }
}