package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import second_in_command.SCUtils
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.getChildrenCopy
import second_in_command.misc.getParent
import second_in_command.ui.SCSkillMenuPanel

class SkillPanelReplacerScript : EveryFrameScript {

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun advance(amount: Float) {

        if (!Global.getSector().isPaused) return
        if (!SCUtils.getSCData().isModEnabled) return

        var state = AppDriver.getInstance().currentState
        if (state !is CampaignState) return

        var core: UIPanelAPI? = null

        var docked = false


        var dialog = ReflectionUtils.invoke("getEncounterDialog", state)
        if (dialog != null)
        {
            docked = true
            core = ReflectionUtils.invoke("getCoreUI", dialog) as UIPanelAPI?
        }

        if (core == null) {
            core = ReflectionUtils.invoke("getCore", state) as UIPanelAPI?
        }

        if (core == null) return

        var corePanels = core.getChildrenCopy().filter { it is UIPanelAPI } as List<UIPanelAPI>
        var innerPanels = corePanels.map { it.getChildrenCopy().find { children -> ReflectionUtils.hasMethodOfName("canReassign", children) }}
        var panel = innerPanels.filterNotNull().firstOrNull() ?: return
        var parent = panel.getParent() ?: return

        parent.removeComponent(panel)

        var scData = SCUtils.getSCData()
        var skillPanel = SCSkillMenuPanel(parent, scData, docked)
        skillPanel.init()
    }

}