package second_in_command.misc

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils

class SCSettings : LunaSettingsListener {


    companion object {

        var baseMaxLevel = 5

        fun getMaxLevel() : Int  {
            var level = baseMaxLevel
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

            /*2500000f, //LV5, “Associates” Background only.*/

          /*  0f, //LV0
            80000f, //LV1
            160000f, //LV2
            480000f, //LV3
            1440000f, //LV4*/
        )
        var canNPCsSpawnWithSkills = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_canNPCsSpawnWithSkills")!!
        var xpGainMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_officerXPMult")!!

        var playerXPMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_playerXPMult")!!

        var difficulty = LunaSettings.getString(SCUtils.MOD_ID, "sc_fleetDifficulty")

        @JvmStatic
        var playerMaxLevel = LunaSettings.getInt(SCUtils.MOD_ID, "sc_playerMaxLevel")


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
        canNPCsSpawnWithSkills = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_canNPCsSpawnWithSkills")!!
        xpGainMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_officerXPMult")!!

        playerXPMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_playerXPMult")!!
        Global.getSettings().setFloat("xpGainMult", playerXPMult)

        difficulty = LunaSettings.getString(SCUtils.MOD_ID, "sc_fleetDifficulty")

        playerMaxLevel = LunaSettings.getInt(SCUtils.MOD_ID, "sc_playerMaxLevel")


        highConstrastIcons = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_highContrast")
        unrestrictedAssociates = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_unrestrictedAssociates")

        reducedDmodOverlay = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_reducedDmodOverlay")!!
        spawnWithTransverse = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideTransverse")!!
        spawnWithNeutrino = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideNeutrino")!!
        spawnWithRemoteSurvey = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideRemote")!!
    }


}