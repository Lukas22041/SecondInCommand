package second_in_command.misc

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils

class SCSettings : LunaSettingsListener {


    companion object {

        var maxLevel = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_officerMaxLevel")!! //Grabs Max level as an Int from Lunalib Config (I think? I don't actually know Java, Kotlin or anything that's going on right now. Where am i again?)

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
            4050000f, //LV5 //Assuming that the x3 modifier per level starting at LV3 stays true
            12150000f, //LV6
            36450000f, //LV7
            109350000f, //LV8
            328050000f, //LV9
            984150000f  //LV10 //Unsure if this is needed since it cut off at Level 4 by default.
            // A better idea might be to have some math that goes: "LVCost = LVCost_Base * (3^level)"
            //Idk how to implement that though, like I said, I don't actually know any of these languages. Idek if I'll be able to get the src to compile properly lel

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


        spawnWithTransverse = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideTransverse")!!
        spawnWithNeutrino = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideNeutrino")!!
        spawnWithRemoteSurvey = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_provideRemote")!!
    }


}
