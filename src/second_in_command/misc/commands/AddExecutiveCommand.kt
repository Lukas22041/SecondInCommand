package second_in_command.misc.commands

import org.lazywizard.console.BaseCommand
import org.lazywizard.console.BaseCommandWithSuggestion
import org.lazywizard.console.CommonStrings
import org.lazywizard.console.Console
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

class AddExecutiveCommand : BaseCommandWithSuggestion {
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
        var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }

        var input = args.lowercase()

        var officers = ArrayList<SCOfficer>()


        if (input == "all") {
            for (aptitude in aptitudes) {
                if (aptitude.tags.contains("restricted")) continue
                var officer = SCUtils.createRandomSCOfficer(aptitude.id)
                officers.add(officer)
            }
        } else {
            var aptitude = aptitudes.find { it.name.lowercase() == args || it.id.lowercase() == args }
            if (aptitude != null && !aptitude.tags.contains("restricted")) {
                var officer = SCUtils.createRandomSCOfficer(aptitude.id)
                officers.add(officer)
            }
        }



        if (officers.isEmpty()) {
            Console.showMessage("Missing/Invalid Aptitude Name/ID. Use the \"ListAptitudes\" command to view all available aptitudes. Use \"all\" instead of an aptitude if you want to receive an officer of all types")
            return BaseCommand.CommandResult.BAD_SYNTAX
        }

        Console.showMessage("")
        for (officer in officers) {
            data.addOfficerToFleet(officer)

            Console.showMessage("Added Executive Officer ${officer.person.nameString} (${officer.getAptitudePlugin().name}) to the fleet")
        }
        Console.showMessage("")



        return BaseCommand.CommandResult.SUCCESS
    }

    override fun getSuggestions(parameter: Int, previous: MutableList<String>?, context: BaseCommand.CommandContext?): MutableList<String> {
        if (parameter != 0) return ArrayList()
        var list = SCSpecStore.getAptitudeSpecs().filter { !it.getPlugin().tags.contains("restricted") }.map { it.id }.toMutableList()
        list.add("all")
        return list
    }
}