package second_in_command.patches

import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CommDirectoryEntry
import patchlib.api.context.AfterContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.FieldMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.After
import patchlib.api.patch.Patch
import patchlib.api.query.FieldQuery
import second_in_command.misc.getChildrenCopy
import second_in_command.specs.SCSpecStore

/** Patch the visual element that displays people in the comm directory, identified by the fact that it is located in
 * "com.fs.starfarer.ui.newui" and has a "CommDirectoryEntry" field */
@Patch(target = ClassMatch(targetPackage = "com.fs.starfarer.ui.newui", fieldMatches = [FieldMatch(type = CommDirectoryEntry::class)]))
object SCCommDirectoryRecolorPatch {

    var query = FieldQuery.create().fieldType(CommDirectoryEntry::class.java).build();

    @JvmStatic
    @After(target = MethodMatch(methodName = "sizeChanged"))
    public fun afterSizeChanged(context: AfterContext) {
        var panel = context.getInferredSelf<UIPanelAPI>()
        var entry = context.getField<CommDirectoryEntry?>(query).get() ?: return
        if (entry.entryData !is PersonAPI) return
        val person = entry.entryData as PersonAPI

        var aptitudeId = person.memoryWithoutUpdate.getString("\$sc_officer_aptitude") ?: return
        var aptitude = SCSpecStore.getAptitudeSpec(aptitudeId)!!.getPlugin()
        var color = aptitude.color

        for (panelChild in panel.getChildrenCopy()) {
            if (panelChild is LabelAPI && panelChild.text.contains("Executive Officer")) {
                panelChild.text = "Executive Officer (${aptitude.name})"
                panelChild.setHighlight("${aptitude.name}")
                panelChild.setHighlightColors(color)
            }
        }
    }
}