package second_in_command.ui.intel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.misc.getAndLoadSprite
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection


class SectorSeedIntel() : BaseIntelPlugin() {


    init {
        isImportant = false

    }

    override fun getName(): String? {
        var name = "Domain sector registry"
        return name
    }

    override fun addBulletPoints(info: TooltipMakerAPI?, mode: IntelInfoPlugin.ListInfoMode?, isUpdate: Boolean, tc: Color?, initPad: Float) {

        var seed = Global.getSector().seedString
        info!!.addSpacer(2f)
        var para = info!!.addPara("ID: $seed", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "")
        info.addPara("Click to copy the seed to clipboard", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "")

    }

    override fun createSmallDescription(info: TooltipMakerAPI?, width: Float, height: Float) {
        info!!.addSpacer(10f)
    }

    override fun hasSmallDescription(): Boolean {

        var seed = Global.getSector().seedString
        val stringSelection = StringSelection(seed)
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(stringSelection, null)

        Global.getSector().campaignUI.messageDisplay.addMessage("Copied sector seed to clipboard")

        return false
    }

    override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        val tags: MutableSet<String> = LinkedHashSet()
        tags.add("Personal")
        return tags
    }

    override fun getIcon(): String {
        var path = "graphics/icons/intel/multiple_new.png"
        Global.getSettings().getAndLoadSprite(path)
        return path
    }

}
