package second_in_command.patches

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUIAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import lunalib.lunaUtil.LunaCommons
import patchlib.api.context.AfterContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.After
import patchlib.api.patch.Patch
import patchlib.api.query.MethodQuery
import second_in_command.SCUtils
import second_in_command.ui.SCSkillMenuPanel
import second_in_command.ui.tutorial.TutorialOverlayPlugin
import second_in_command.ui.tutorial.TutorialStep

/**
 * Patches the "showPanelAsDialog" method in the core ui class, which is called when the skill menu is being attached.
 * Same method is called for other panels too, so it checks if the attached panel has the method "canReassign", which only the skill menu has.
 * */
@Patch(target = ClassMatch(targetPackage = "com.fs.starfarer.ui.newui", subtype = CoreUIAPI::class))
object SCSkillMenuReplacementPatch {

    var panelQuery = MethodQuery.create().methodName("canReassign").build();
    var getParentQuery = MethodQuery.create().methodName("getParent").build();

    @JvmStatic
    @After(target = MethodMatch("showPanelAsDialog", parameterCount = 6))
    public fun afterShowPanelAsDialog(context: AfterContext) {
        val panel = context.getArg(1) ?: return

        if (!context.hasMethod(panelQuery, panel)) return
        if (panel !is UIPanelAPI) return

        var parent = context.getMethod(getParentQuery, panel).call<UIPanelAPI?>() ?: return

        parent.removeComponent(panel)

        var scData = SCUtils.getPlayerData()
        var skillPanel = SCSkillMenuPanel(parent, scData, false)
        skillPanel.init()

        //Tutorial
        var tutorialKey = "hasSeenTutorial"
        if (LunaCommons.get("second_in_command", tutorialKey) != true) {
            val steps = TutorialStep.buildDefaultSteps(
                isCompact   = skillPanel.isUseCompactLayout(),
                panelWidth  = skillPanel.width,
                panelHeight = skillPanel.height
            )
            val plugin  = TutorialOverlayPlugin(parent, steps, skillPanel.width, skillPanel.height)
            val overlay = Global.getSettings().createCustom(skillPanel.width, skillPanel.height, plugin)
            plugin.panel = overlay
            parent.addComponent(overlay)
            overlay.position.inTL(0f, 0f)
            // When a demo step changes game state it calls this to refresh the skill panel
            // and re-raise the overlay so it still renders on top.
            plugin.onRefreshPanel = {
                skillPanel.recreatePanel()
                parent.removeComponent(overlay)
                parent.addComponent(overlay)
                overlay.position.inTL(0f, 0f)
            }
            plugin.rebuildTextBox()

            LunaCommons.set("second_in_command", tutorialKey, true)
        }
    }
}