package second_in_command.misc.snippets

import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaDebug.LunaSnippet
import lunalib.lunaDebug.SnippetBuilder
import second_in_command.SCUtils
import second_in_command.specs.SCSpecStore

class AddAllOfficersSnippet : LunaSnippet() {
    override fun getName(): String {
        return "Add all types of executive officers."
    }

    override fun getDescription(): String {
        return "Adds an officer of every aptitude to the fleet."
    }

    override fun getModId(): String {
        return SCUtils.MOD_ID
    }

    override fun getTags(): MutableList<String> {
        return mutableListOf(LunaSnippet.SnippetTags.Debug.name)
    }

    override fun addParameters(builder: SnippetBuilder?) {
        super.addParameters(builder)
    }

    override fun execute(parameters: MutableMap<String, Any>?, output: TooltipMakerAPI) {
        super.execute(parameters, output)

        var data = SCUtils.getPlayerData()
        var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }

        for (aptitude in aptitudes) {
            if (aptitude.getId() == "sc_fake_combat_aptitude") continue
            if (aptitude.tags.contains("restricted")) continue

            var officer = SCUtils.createRandomSCOfficer(aptitude.getId())
            data.addOfficerToFleet(officer)

            output.addPara("Added ${officer.person.nameString} (${aptitude.getName()}) to the fleet", 0f, Misc.getPositiveHighlightColor(), aptitude.getColor(),
                "${aptitude.getName()}")

        }
    }

}