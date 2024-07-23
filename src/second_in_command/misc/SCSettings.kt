package second_in_command.misc

import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import second_in_command.SCUtils

class SCSettings : LunaSettingsListener {


    companion object {
        var isModEnabled = true
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