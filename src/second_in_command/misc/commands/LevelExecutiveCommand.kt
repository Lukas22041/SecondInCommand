package second_in_command.misc.commands

import org.lazywizard.console.BaseCommand
import org.lazywizard.console.BaseCommandWithSuggestion
import org.lazywizard.console.CommonStrings
import org.lazywizard.console.Console
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

class LevelExecutiveCommand : BaseCommandWithSuggestion {
    /**
     * Called when the player enters your command.
     *
     * @param args    The arguments passed into this command. Will be an empty [String] if no arguments were
     * entered.
     * @param context Where this command was called from (campaign, combat, mission, simulation, etc).
     *
     * @return A [CommandResult] describing the result of execution.
     *
     * @since 2.0
     */
    override fun runCommand(args: String, context: BaseCommand.CommandContext): BaseCommand.CommandResult {

        if (!context.isInCampaign) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY)
            return BaseCommand.CommandResult.WRONG_CONTEXT
        }

        var data = SCUtils.getPlayerData()
        var officers = data.getOfficersInFleet()
        var toAffect = ArrayList<SCOfficer>()

        if (officers.isEmpty()) {
            Console.showMessage("There are no officers in the players fleet")
            return BaseCommand.CommandResult.ERROR
        }

        var input = args.lowercase()

        if (input == "all") {
            toAffect.addAll(officers)
        } else {
            var officer = officers.find { it.person.nameString.lowercase().contains(input) }
            if (officer != null) {
                toAffect.add(officer)
            }
        }

        if (toAffect.isEmpty()) {
            Console.showMessage("Could not find an officer with this input. Make sure to write the executive officers full name, or write \"all\" to increase the level of all available officers")
            return BaseCommand.CommandResult.BAD_SYNTAX
        }

        Console.showMessage("")
        for (officer in toAffect) {
            officer.increaseLevel(1)
            Console.showMessage("Leveled up ${officer.person.nameString} to level ${officer.getCurrentLevel()}")
        }
        Console.showMessage("")

        return BaseCommand.CommandResult.SUCCESS
    }

    override fun getSuggestions(parameter: Int, previous: MutableList<String>, context: BaseCommand.CommandContext): MutableList<String> {
        if (parameter != 0 || !context.isInCampaign) return ArrayList()
        var list = SCUtils.getPlayerData().getOfficersInFleet().map { it.person.nameString }.toMutableList()
        list.add("all")
        return list
    }
}