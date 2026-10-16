package second_in_command.patches

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.coreui.CaptainPickerDialog
import patchlib.api.context.MethodCallContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.FieldMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.Patch
import patchlib.api.patch.RedirectMethodCall
import patchlib.api.query.MethodQuery

/** Patches the entries of the captain picker in "com.fs.starfarer.coreui", identifies as the class that has the "CaptainPickerDialog" field.
 * Required for neural junction to correctly prevent unequipping unremovable AI cores by clicking on the player,
 * and returning the unequipped AI core in to the inventory. */
@Patch(target = ClassMatch(packageName = "com.fs.starfarer.coreui", subtype = UIPanelAPI::class,
    fieldMatches = [FieldMatch(type = CaptainPickerDialog::class)]))
object SCNeuralJunctionPickerEntryPatch {

    var showUnremovableQuery = MethodQuery.create().methodName("showUnremovable").parameterCount(1).build()

    @JvmStatic
    @RedirectMethodCall(
        target = MethodMatch(methodName = "actionPerformed", parameterCount = 2),
        call = MethodMatch(methodName = "setFlagship", parameterTypes = [FleetMemberAPI::class])
    )
    fun redirectSetFlagship(context: MethodCallContext) {
        val member = context.getCallArg(0) as FleetMemberAPI
        val core = member.captain

        //Only the player replacing an AI core needs handling, which only Neural Junction makes possible
        if (!core.isAICore) {
            context.call()
            return
        }

        if (Misc.isUnremovable(core)) {
            //Method on the captain picker entry that displays the "error" message for unremovable ai-cores
            context.getMethod(showUnremovableQuery).call<Any?>(core)
            return
        }

        Global.getSector().playerFleet.cargo.addCommodity(core.aiCoreId, 1f)
        context.call()
    }
}
