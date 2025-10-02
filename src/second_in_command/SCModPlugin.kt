package second_in_command

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaSettings.LunaSettings
import second_in_command.misc.NPCFleetInflater
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.SCSettings
import second_in_command.misc.SpecialEventHandler
import second_in_command.misc.backgrounds.AssociatesBackground
import second_in_command.misc.codex.CodexHandler
import second_in_command.misc.snippets.AddAllOfficersSnippet
import second_in_command.misc.snippets.AddXPToOfficersSnippet
import second_in_command.scripts.*
import second_in_command.skills.engineering.scripts.CompactStorageScript
import second_in_command.specs.SCSpecStore
import second_in_command.ui.intel.SectorSeedIntel
import java.lang.Exception

class SCModPlugin : BaseModPlugin() {

    init {
        //Provide a better crash message when using an outdated version, not just the "missing interface" one
        var console = Global.getSettings().modManager.getModSpec("lw_console")
        if (console != null) {
            if (console.version.contains("2024") || console.version.contains("2025") || console.version.contains("2023")) {
                throw Exception("\n\nYour version of console commands (${console.version}) is outdated. " +
                        "Version 4.0.4 or above is required. Older versions used a different version format, and mod-managers may not link to the correct version. \n")
            }
        }
    }

    override fun onAboutToStartGeneratingCodex() {
        CodexHandler.onAboutToStartGeneratingCodex()
    }

    override fun onAboutToLinkCodexEntries() {
        CodexHandler.onAboutToLinkCodexEntries()
    }

    override fun onCodexDataGenerated() {
        CodexHandler.onCodexDataGenerated()
    }

    override fun onApplicationLoad() {
        LunaSettings.addSettingsListener(SCSettings())

        LunaDebug.addSnippet(AddAllOfficersSnippet())
        LunaDebug.addSnippet(AddXPToOfficersSnippet())

        SCSpecStore.loadCategoriesFromCSV()
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
        if (!Global.getSector().playerPerson.stats.hasSkill("sc_utility_skill")) {
            Global.getSector().playerPerson.stats.setSkillLevel("sc_utility_skill", 2f)
        }

        Global.getSettings().setFloat("xpGainMult", SCSettings.playerXPMult)

        if (!Global.getSector().listenerManager.hasListenerOfClass(ExecutiveOfficerCommAdder::class.java)) {
            Global.getSector().listenerManager.addListener(ExecutiveOfficerCommAdder(), false)
        }

        Global.getSector().addTransientScript(SkillPanelReplacerScript())
        Global.getSector().addTransientScript(ControllerHullmodAdderScript())
        Global.getSector().addTransientScript(SCNeuralJunctionScript())
        Global.getSector().addTransientScript(VanillaSkillsDisabler())
        Global.getSector().addTransientScript(AutomatedShipsManager())
        Global.getSector().addTransientScript(CommDirectoryRecolorScript())

        var compactStorageListener = CompactStorageScript()
        Global.getSector().addTransientScript(compactStorageListener)
        Global.getSector().listenerManager.addListener(compactStorageListener, true)
        //Global.getSector().addTransientScript(SectorSeedAdderScript())
        Global.getSector().listenerManager.addListener(NPCFleetInflater(), true)

        Global.getSector().addTransientListener(SCCampaignEventListener())
        Global.getSector().addTransientScript(SCXPTracker())

        Global.getSector().registerPlugin(SCCampaignPlugin())

        //Add Abilities that no longer have a skill
        if (SCSettings.spawnWithTransverse && !Global.getSector().characterData.abilities.contains(Abilities.TRANSVERSE_JUMP)) {
            Global.getSector().characterData.addAbility(Abilities.TRANSVERSE_JUMP)
        }

        if (SCSettings.spawnWithNeutrino && !Global.getSector().characterData.abilities.contains(Abilities.GRAVITIC_SCAN)) {
            Global.getSector().characterData.addAbility(Abilities.GRAVITIC_SCAN)
        }

        if (SCSettings.spawnWithRemoteSurvey && !Global.getSector().characterData.abilities.contains(Abilities.REMOTE_SURVEY)) {
            Global.getSector().characterData.addAbility(Abilities.REMOTE_SURVEY)
        }

        SpecialEventHandler.checkEvents()

        if (!Global.getSector().intelManager.hasIntelOfClass(SectorSeedIntel::class.java)) {
            Global.getSector().intelManager.addIntel(SectorSeedIntel(), true)
        }

        if (Global.getSettings().modManager.isModEnabled("nexerelin")) {
            AssociatesBackground.fillMissingSlot()
        }

        SCUtils.getPlayerData().remove4thOfficer()
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