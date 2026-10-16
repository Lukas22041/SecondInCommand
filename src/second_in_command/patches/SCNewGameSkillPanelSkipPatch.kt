package second_in_command.patches

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.title.TitleScreenState
import patchlib.api.context.MethodCallContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.Patch
import patchlib.api.patch.RedirectMethodCall

/** Skips the skill panel appearing during new game creation, by setting the skill point check to 0, as the game skips the
 * screen if the player has no skill points. Avoids having to do a more janky work around of removing the players actual skill point */
@Patch(target = ClassMatch(type = TitleScreenState::class))
object SCNewGameSkillPanelSkipPatch {

    @JvmStatic
    @RedirectMethodCall(
        target = MethodMatch(methodName = "dialogDismissed", parameterCount = 2),
        owner = ClassMatch(subtype = MutableCharacterStatsAPI::class),
        call = MethodMatch(methodName = "getPoints")
    )
    fun skipInitialSkillSelection(context: MethodCallContext) {
        context.setResult(0)
    }
}
