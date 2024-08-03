package second_in_command.misc.snippets

import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaDebug.LunaSnippet
import lunalib.lunaDebug.SnippetBuilder
import second_in_command.SCUtils

class AddXPToOfficersSnippet : LunaSnippet() {
    override fun getName(): String {
        return "Add xp to all Executive Officers."
    }

    override fun getDescription(): String {
        return "Adds experience points to all officers. XP Reduction for inactive officers still apply."
    }

    override fun getModId(): String {
        return SCUtils.MOD_ID
    }

    override fun getTags(): MutableList<String> {
        return mutableListOf(LunaSnippet.SnippetTags.Debug.name)
    }

    override fun addParameters(builder: SnippetBuilder) {
        super.addParameters(builder)
        builder.addFloatParameter("XP", "sc_xp", 1000f, 0f, 1000000f)
    }

    override fun execute(parameters: MutableMap<String, Any>, output: TooltipMakerAPI) {
        super.execute(parameters, output)

        var data = SCUtils.getPlayerData()
        var xp = parameters.get("sc_xp") as Float

        for (officer in data.getOfficersInFleet()) {
            officer.addXP(xp)
        }
    }

}