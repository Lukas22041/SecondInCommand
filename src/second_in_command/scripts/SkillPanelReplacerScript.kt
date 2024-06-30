package second_in_command.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
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

        var state = AppDriver.getInstance().currentState
        if (state !is CampaignState) return

        var core = ReflectionUtils.invoke("getCore", state) as UIPanelAPI ?: return

        var corePanels = core.getChildrenCopy().filter { it is UIPanelAPI } as List<UIPanelAPI>
        var innerPanels = corePanels.map { it.getChildrenCopy().find { children -> ReflectionUtils.hasMethodOfName("canReassign", children) }}
        var panel = innerPanels.filterNotNull().firstOrNull() ?: return
        var parent = panel.getParent() ?: return

        parent.removeComponent(panel)

        var skillPanel = SCSkillMenuPanel(parent)
        skillPanel.init()
    }

}