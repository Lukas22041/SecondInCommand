package second_in_command

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaSettings.LunaSettings
import second_in_command.misc.NPCFleetInflater
import second_in_command.misc.SCSettings
import second_in_command.misc.snippets.AddAllOfficersSnippet
import second_in_command.misc.snippets.AddXPToOfficersSnippet
import second_in_command.scripts.*
import second_in_command.specs.SCSpecStore
import java.lang.Exception

class SCModPlugin : BaseModPlugin() {

    override fun onApplicationLoad() {

        LunaSettings.addSettingsListener(SCSettings())

        LunaDebug.addSnippet(AddAllOfficersSnippet())
        LunaDebug.addSnippet(AddXPToOfficersSnippet())

        SCSpecStore.loadAptitudeSpecsFromCSV()
        SCSpecStore.loadSkillSpecsFromCSV()

        checkForIncompatibilities()
    }

    fun checkForIncompatibilities() {
        var incompatibleIds = ArrayList<String>()
        var loadedMods = Global.getSettings().modManager.enabledModsCopy

        incompatibleIds.add("pantera_ANewLevel20")
        incompatibleIds.add("pantera_ANewLevel25")
        incompatibleIds.add("pantera_ANewLevel30")
        incompatibleIds.add("pantera_ANewLevel40")

        incompatibleIds.add("pantera_ANewLevel20R")
        incompatibleIds.add("pantera_ANewLevel25R")
        incompatibleIds.add("pantera_ANewLevel30R")
        incompatibleIds.add("pantera_ANewLevel40R")

        incompatibleIds.add("QualityCaptains")
        incompatibleIds.add("TrulyAutomatedShips")
        incompatibleIds.add("adjustable_skill_thresholds")
        incompatibleIds.add("ESP_Skill_Overhaul")


        var incompatibleMods = loadedMods.filter { incompatibleIds.contains(it.id) }

        if (incompatibleMods.isNotEmpty()) {

            var text = ""
            text += "The \"Second-in-Command\" mod is incompatible with some of the mods shown below due to modifying the same features."
            text+= "\n\n"
            for (mod in incompatibleMods) {
                text+= " - \"${mod.name}\"\n"
            }
            text += "\n"
            text += "Disable those mods, or \"Second-in-Command\" to get the game launchable."

            throw Exception(text)
        }
    }

    override fun onGameLoad(newGame: Boolean) {
        Global.getSector().addTransientScript(SkillPanelReplacerScript())
        Global.getSector().addTransientScript(ControllerHullmodAdderScript())
        Global.getSector().addTransientScript(SCNeuralJunctionScript())
        Global.getSector().addTransientScript(VanillaSkillsDisabler())
        Global.getSector().listenerManager.addListener(NPCFleetInflater(), true)

        Global.getSector().addTransientListener(SCCampaignEventListener())

        Global.getSector().registerPlugin(SCCampaignPlugin())

        //Add Abilities that no longer have a skill
        if (!Global.getSector().characterData.abilities.contains(Abilities.TRANSVERSE_JUMP)) {
            Global.getSector().characterData.addAbility(Abilities.TRANSVERSE_JUMP)
        }

        if (!Global.getSector().characterData.abilities.contains(Abilities.GRAVITIC_SCAN)) {
            Global.getSector().characterData.addAbility(Abilities.GRAVITIC_SCAN)
        }

        if (!Global.getSector().characterData.abilities.contains(Abilities.REMOTE_SURVEY)) {
            Global.getSector().characterData.addAbility(Abilities.REMOTE_SURVEY)
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

    }
}