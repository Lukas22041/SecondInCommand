package second_in_command.scripts

import com.fs.starfarer.api.campaign.CharacterDataAPI
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin
import com.fs.starfarer.api.combat.ViewportAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignEngine
import com.fs.starfarer.campaign.PlayerCharacterData
import com.fs.starfarer.title.TitleScreenState
import com.fs.state.AppDriver
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.ReflectionUtils
import second_in_command.misc.SCSettings
import second_in_command.misc.getChildrenCopy
import second_in_command.ui.SCSkillMenuPanel

class SkillPanelReplacerScriptTitlescreen : EveryFrameCombatPlugin {

    override fun init(engine: CombatEngineAPI?) {

    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {

    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {

        if (!SCSettings.isModEnabled) return


        var state = AppDriver.getInstance().currentState
        if (state !is TitleScreenState) return

        var core = ReflectionUtils.invoke("getScreenPanel", state) as UIPanelAPI ?: return


        var panelsWithTab = core.getChildrenCopy().filter { ReflectionUtils.hasMethodOfName("getTab", it) } as List<UIPanelAPI>

        //com.fs.starfarer.campaign.save.if
        var parent = panelsWithTab.find {  ReflectionUtils.hasMethodOfName("canReassign", ReflectionUtils.invoke("getTab", it)!! ) } ?: return

        var panel = ReflectionUtils.invoke("getTab", parent) as UIPanelAPI

        panel.position.inTL(0f, 5f)

        var children = panel.getChildrenCopy()

        for (child in children) {
            if (child == children[7]) {
                child.position.inTMid(140f)
                continue
            }
            child.position.inTL(10000f, 0f)
        }



        var dataFieldName = ReflectionUtils.getFieldsOfType(panel, PlayerCharacterData::class.java).first()
        var data = ReflectionUtils.get(dataFieldName, panel) as CharacterDataAPI

        var scData = SCData(data.person)

        if (data.memoryWithoutUpdate.contains(SCUtils.DATA_KEY)) return

        data.memoryWithoutUpdate.set(SCUtils.DATA_KEY, scData)

        var skillPanel = SCSkillMenuPanel(parent, scData, false, true)
        skillPanel.init()



    }

    override fun renderInWorldCoords(viewport: ViewportAPI?) {

    }

    override fun renderInUICoords(viewport: ViewportAPI?) {

    }

}