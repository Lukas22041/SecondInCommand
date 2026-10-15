package second_in_command.patches

import com.fs.graphics.Sprite
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.fleet.CampaignFleet
import patchlib.api.context.AfterContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.FieldMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.After
import patchlib.api.patch.Patch
import second_in_command.SCUtils
import second_in_command.ui.elements.ScrapWidget

/** Attaches the scrap widget to the campaign ability bar. The bar is identified as the class in "com.fs.starfarer.ui.newui"
 * that has both a "CampaignFleet" and a "Sprite" field. */
@Patch(target = ClassMatch(packageName = "com.fs.starfarer.ui.newui", subtype = UIPanelAPI::class,
    fieldMatches = [FieldMatch(type = CampaignFleet::class), FieldMatch(type = Sprite::class)]))
object SCScavengingHotbarPatch {

    private const val WIDGET_KEY = "sc_scavenging_scrap_widget"

    @JvmStatic
    @After(target = MethodMatch(methodName = "advanceImpl", parameterCount = 1))
    public fun afterAdvanceImpl(context: AfterContext) {
        if (Global.getSector()?.playerFleet == null) return

        val data = context.data
        var panel = data.get<CustomPanelAPI?>(WIDGET_KEY)

        if (panel == null) {
            val abilityPanel = context.getInferredSelf<UIPanelAPI>()
            panel = createWidget(abilityPanel)
            data.putWeak(WIDGET_KEY, panel)
        }

        panel.opacity = if (SCUtils.getPlayerData().isAptitudeActive("sc_scavenging")) 1f else 0f
    }

    private fun createWidget(abilityPanel: UIPanelAPI) : CustomPanelAPI {
        val width = 200f
        val height = 11f
        val panel = Global.getSettings().createCustom(width, height, null)
        abilityPanel.addComponent(panel)
        panel.position.inTL(-8f, -height -40f)

        val element = panel.createUIElement(width, height, false)
        panel.addUIElement(element)

        ScrapWidget(element)

        val hintsPanel = Global.getSector().campaignUI.hintPanel
        if (hintsPanel is UIPanelAPI) {
            hintsPanel.position.setYAlignOffset(135f)
            hintsPanel.position.setXAlignOffset(480f)
        }

        return panel
    }
}
