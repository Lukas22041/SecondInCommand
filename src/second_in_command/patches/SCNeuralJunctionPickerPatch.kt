package second_in_command.patches

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.coreui.CaptainPickerDialog
import com.fs.starfarer.rpg.OfficerData
import com.fs.starfarer.rpg.Person
import patchlib.api.context.MethodCallContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.Patch
import patchlib.api.patch.RedirectMethodCall
import second_in_command.SCUtils

/** Neural Junction: Adds the player to the list of AI cores the captain picker offers for automated ships by adding the player to the list of cores. */
@Patch(target = ClassMatch(type = CaptainPickerDialog::class))
object SCNeuralJunctionPickerPatch {

    @JvmStatic
    @RedirectMethodCall(
        target = MethodMatch(methodName = "sizeChanged", parameterCount = 2),
        call = MethodMatch(methodName = "isEmpty", parameterCount = 0)
    )
    public fun addPlayerToAICoreList(context: MethodCallContext) {
        if (SCUtils.getPlayerData().isSkillActive("sc_automated_neural_junction")) {
            var player = Global.getSector().playerPerson as Person
            player.memoryWithoutUpdate.set(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT, 1f)

            var list = context.getInferredCallReceiver<MutableList<Any>>()
            list.add(0, OfficerData(player))
        }

        var isEmpty = context.call()
        context.setResult(isEmpty)
    }
}
