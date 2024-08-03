package second_in_command.misc

import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils

class SCSettings : LunaSettingsListener {


    companion object {
        var isModEnabled = true

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
        var baseXpGainMult = 0.5f
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
        isModEnabled = !LunaSettings.getBoolean(SCUtils.MOD_ID, "sc_DisableMod")!!
    }


}