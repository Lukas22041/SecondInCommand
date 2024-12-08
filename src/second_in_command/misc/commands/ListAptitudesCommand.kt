package second_in_command.misc.commands

import org.lazywizard.console.BaseCommand
import org.lazywizard.console.CommonStrings
import org.lazywizard.console.Console
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

class ListAptitudesCommand : BaseCommand {
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

        var aptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }.filter { !it.tags.contains("restricted") }

        Console.showMessage("")
        for (aptitude in aptitudes) {
            Console.showMessage("${aptitude.name} - ${aptitude.id}")
        }
        Console.showMessage("")

        return BaseCommand.CommandResult.SUCCESS
    }
}