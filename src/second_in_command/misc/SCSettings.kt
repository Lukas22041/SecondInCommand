package second_in_command.misc

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils

class SCSettings : LunaSettingsListener {


    companion object {

        var maxLevel = 5

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
        )
        var canNPCsSpawnWithSkills = LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_canNPCsSpawnWithSkills")!!
        var xpGainMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_officerXPMult")!!

        var playerXPMult = LunaSettings.getFloat(SCUtils.MOD_ID, "sc_playerXPMult")!!

        var difficulty = LunaSettings.getString(SCUtils.MOD_ID, "sc_fleetDifficulty")
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

    }


}