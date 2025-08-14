package second_in_command.misc

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils
import second_in_command.misc.backgrounds.AssociatesBackground

class SCSettings : LunaSettingsListener {

    enum class CommRarity {
        None, Rare, Normal, Common
    }

    enum class DerelictRarity {
        None, Rare, Normal, Common
    }

    companion object {

        private var baseMaxLevel = 5
        fun getMaxLevel() : Int  {
            var level = baseMaxLevel
            if (additionalLevel) level += 1
            return level
        }

        var xpPerLevel = listOf<Float>(
           /* 0f, //LV0
            75000f, //LV1
            150000f, //LV2
            300000f, //LV3
            600000f, //LV4*/

            0f, //LV0
            75000f, //LV1
            150000f, //LV2
            450000f, //LV3
            1350000f, //LV4

            2500000f, //LV5,

          /*  0f, //LV0
            80000f, //LV1
            160000f, //LV2
            480000f, //LV3
            1440000f, //LV4*/
        )


        var enable4thSlot = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_enable4thSlot")!!
        var additionalLevel = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_enableAdditionalLevel")!!
        var xpGainMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_officerXPMult")!!


        var startBarEventEnabled = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_startEvent")!!
        var commRarity = when(LunaSettings.getString(SCUtils.MOD_ID, "sc_officerCommRarity")!!) {
            "None" -> CommRarity.None
            "Rare" -> CommRarity.Rare
            "Normal" -> CommRarity.Normal
            "Common" -> CommRarity.Common
            else -> CommRarity.Normal
        }
        var derelictRarity = when(LunaSettings.getString(SCUtils.MOD_ID, "sc_officerDerelictRarity")!!) {
            "None" -> DerelictRarity.None
            "Rare" -> DerelictRarity.Rare
            "Normal" -> DerelictRarity.Normal
            "Common" -> DerelictRarity.Common
            else -> DerelictRarity.Normal
        }



        var progressionMode = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_progressionMode")!!
        var progressionSlot1Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot1")
        var progressionSlot2Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot2")
        var progressionSlot3Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot3")
        var progressionSlot4Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot4")



        var playerXPMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_playerXPMult")!!

        var canNPCsSpawnWithSkills = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_canNPCsSpawnWithSkills")!!
        var difficulty = LunaSettings.getString(SCUtils.MOD_ID, "sc_fleetDifficulty")

        @JvmStatic
        var playerMaxLevel = LunaSettings.getInt(SCUtils.MOD_ID, "sc_playerMaxLevel")

        @JvmStatic
        var autoPointsMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_autoPointsMult")!!

        //Misc
        var highConstrastIcons = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_highContrast")
        var unrestrictedAssociates = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_unrestrictedAssociates")


        var reducedDmodOverlay = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_reducedDmodOverlay")!!
        var spawnWithTransverse = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideTransverse")!!
        var spawnWithNeutrino = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideNeutrino")!!
        var spawnWithRemoteSurvey = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideRemote")!!

    }

    init {
        applySettings()
    }

    override fun settingsChanged(modID: String) {
        if (modID == SCUtils.MOD_ID) {
            applySettings()
        }
    }

    fun applySettings() {
        xpGainMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_officerXPMult")!!

        enable4thSlot = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_enable4thSlot")!!
        if (Global.getCurrentState() == GameState.CAMPAIGN) {
            if (Global.getSettings().modManager.isModEnabled("nexerelin")) {
                AssociatesBackground.fillMissingSlot()
            }
            SCUtils.getPlayerData().remove4thOfficer()
        }
        additionalLevel = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_enableAdditionalLevel")!!
        progressionMode = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_progressionMode")!!

        startBarEventEnabled = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_startEvent")!!
        commRarity = when(LunaSettings.getString(SCUtils.MOD_ID, "sc_officerCommRarity")!!) {
            "None" -> CommRarity.None
            "Rare" -> CommRarity.Rare
            "Normal" -> CommRarity.Normal
            "Common" -> CommRarity.Common
            else -> CommRarity.Normal
        }
        derelictRarity = when(LunaSettings.getString(SCUtils.MOD_ID, "sc_officerDerelictRarity")!!) {
            "None" -> DerelictRarity.None
            "Rare" -> DerelictRarity.Rare
            "Normal" -> DerelictRarity.Normal
            "Common" -> DerelictRarity.Common
            else -> DerelictRarity.Normal
        }

        progressionSlot1Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot1")
        progressionSlot2Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot2")
        progressionSlot3Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot3")
        progressionSlot4Level = LunaSettings.getInt(SCUtils.MOD_ID, "sc_progressionLevelSlot4")

        playerXPMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_playerXPMult")!!
        Global.getSettings().setFloat("xpGainMult", playerXPMult)

        canNPCsSpawnWithSkills = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_canNPCsSpawnWithSkills")!!
        difficulty = LunaSettings.getString(SCUtils.MOD_ID, "sc_fleetDifficulty")

        playerMaxLevel = LunaSettings.getInt(SCUtils.MOD_ID, "sc_playerMaxLevel")

        autoPointsMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_autoPointsMult")!!

        highConstrastIcons = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_highContrast")
        unrestrictedAssociates = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_unrestrictedAssociates")

        reducedDmodOverlay = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_reducedDmodOverlay")!!
        spawnWithTransverse = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideTransverse")!!
        spawnWithNeutrino = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideNeutrino")!!
        spawnWithRemoteSurvey = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideRemote")!!
    }


}