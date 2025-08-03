package second_in_command.ui.sim

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.SimulatorPluginImpl
import com.fs.starfarer.api.plugins.SimulatorPlugin
import com.fs.starfarer.api.plugins.SimulatorPlugin.SimOptionData
import com.fs.starfarer.api.plugins.SimulatorPlugin.SimOptionSelectorData
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCUtils
import second_in_command.misc.NPCOfficerGenerator
import second_in_command.misc.SCSettings

class SiCSimPlugin : SimulatorPluginImpl() {

    companion object {
        var EXECUTIVE_ID: String = "second_in_command"
        var EXECUTIVE_NONE = "none"
        var EXECUTIVE_LIGHT = "light"
        var EXECUTIVE_MID = "mid"
        var EXECUTIVE_HIGH = "high"
        var EXECUTIVE_MATCH = "match"
    }



    override fun resetToDefaults(withSave: Boolean) {
        super.resetToDefaults(withSave)
        uiStateData.settings.put(EXECUTIVE_ID, EXECUTIVE_NONE)
        if (withSave) {
            saveUIStateData()
        }
    }

    override fun getSimOptions(category: SimulatorPlugin.SimCategoryData?): MutableList<SimulatorPlugin.AdvancedSimOption> {
        var result = super.getSimOptions(category)

        //Dont appear in missions
        if (Global.getCombatEngine()?.isInCampaignSim == true) {
            var data = SCUtils.getPlayerData() ?: return result
            var skills = data.getAllActiveSkillsPlugins().count()

            var text = "Executive officers are picked to fit the deployed group of ships. Only groups deployed at the same time share the same executive officers."

            val execs = SimOptionSelectorData(EXECUTIVE_ID, "Executive Officers", false)
            execs.options.add(SimOptionData(EXECUTIVE_NONE,"None",
                "No executive officers within the opposing fleet.",
                "officers_none"))
            execs.options.add(SimOptionData(EXECUTIVE_LIGHT, "Low, 5-7 skills",
                "Executive officers with a total of up to 5-7 skills within the opposing fleet." +
                        "\n\n$text",
                "officers_some"))
            execs.options.add(SimOptionData(EXECUTIVE_MID,  "Mid, 11-12 skills",
                "Executive officers with a total of up to 11-12 skills within the opposing fleet." +
                        "\n\n$text",
                "officers_all"))
            execs.options.add(SimOptionData(EXECUTIVE_HIGH, "High, 16 skills",
                "Executive officers with a total of up to 16 skills within the opposing fleet." +
                        "\n\n$text",
                "officers_high"))
            execs.options.add(SimOptionData(EXECUTIVE_MATCH, "Match Player, $skills skills",
                "Executive officers with a total amount of skills similar to how many are active in your fleet. Slightly adjusted based on difficulty settings." +
                        "\n\n$text",
                "sim_executive_match"))
            result.add(execs)
        }



        return result
    }

    override fun applySettingsToFleetMembers(members: MutableList<FleetMemberAPI>,
                                             category: SimulatorPlugin.SimCategoryData,
                                             settings: MutableMap<String, String>) {
        super.applySettingsToFleetMembers(members, category, settings)

        var skillsSetting = settings.get(EXECUTIVE_ID)
        if (skillsSetting != null) {

            if (skillsSetting == EXECUTIVE_NONE) return

            var skillCount = 0
            if (skillsSetting == EXECUTIVE_LIGHT) skillCount = 4
            if (skillsSetting == EXECUTIVE_MID) skillCount = 9
            if (skillsSetting == EXECUTIVE_HIGH) skillCount = 13
            if (skillsSetting == EXECUTIVE_MATCH) {
                var data = SCUtils.getPlayerData() ?: return
                var playerSkills = data.getAllActiveSkillsPlugins().count()
                var activeOfficers = data.getActiveOfficers().count()

                //-1 for each active officers origin, otherwise the opponent would get more skills
                skillCount = playerSkills - activeOfficers
                if (SCSettings.difficulty == "Easy") skillCount-2
                if (SCSettings.difficulty == "Normal") skillCount-1
                skillCount = MathUtils.clamp(skillCount, 0, 15)
            }

            if (skillCount > 0) {
                for (member in members) {
                    var data = member.fleetData ?: continue
                    var fleet = data?.fleet ?: continue
                    fleet.memoryWithoutUpdate.set(NPCOfficerGenerator.SKILL_COUNT_OVERWRITE_KEY, skillCount)
                    if (fleet != null) SCUtils.getFleetData(fleet)
                }
            }

        }


    }

}