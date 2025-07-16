package second_in_command.skills.engineering

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.skills.engineering.scripts.SolidConstructionScript
import second_in_command.specs.SCBaseSkillPlugin
import java.awt.Color

class SolidConstruction : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("The threshold at which low combat readiness causes negative effects is reduced to 30%% (base 50%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("The threshold at which low combat readiness causes malfunctions is reduced to 24%% (base 40%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Ships can no longer acquire more than 2 d-mods through battle", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        //tooltip.addPara("   - Any additional d-mods after the first 2 are automatically removed after battle", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Does not remove d-mods on ships with more than 2 d-mods, but prevents acquiring more", 0f, Misc.getTextColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.dynamic.getStat(Stats.CR_MALFUNCION_RANGE).modifyMult(id, 0.6f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {
        if (data.isPlayer) {
            if (!Global.getSector().hasScript(SolidConstructionScript::class.java)) {
                Global.getSector().addScript(SolidConstructionScript())
            }
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.isPlayer) {
            for (script in ArrayList(Global.getSector().scripts)) {
                if (script is SolidConstructionScript) {
                    Global.getSector().removeScript(script)
                }
            }
        }
    }

}

class SolidConstructionIntel(var removed: ArrayList<Pair<String, String>>) : BaseIntelPlugin() {

    init {
        Global.getSector().addScript(this)
        endAfterDelay(7f)
    }

    override fun notifyEnded() {
        Global.getSector().removeScript(this)
    }

    override fun getName(): String {
        return "Skill - Solid Construction"
    }

    override fun getIcon(): String {
        return "graphics/secondInCommand/engineering/solid_construction.png"
    }

    override fun hasSmallDescription(): Boolean {
        return true
    }

    override fun createSmallDescription(info: TooltipMakerAPI, width: Float, height: Float) {
        info.addSpacer(3f)
        info.addPara("Prevented some ships from acquiring additional d-mods from the latest combat encounter.", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        info.addSpacer(10f)

        for ((ship, hullmod) in removed) {
            info.addPara("Prevented the $ship from acquiring the $hullmod d-mod", 3f, Misc.getTextColor(), Misc.getHighlightColor(), "$ship", "$hullmod")
        }

    }

    override fun addBulletPoints(info: TooltipMakerAPI, mode: IntelInfoPlugin.ListInfoMode?, isUpdate: Boolean, tc: Color?, initPad: Float) {
        //info!!.addPara("${pick.shipName} - removed ${spec.displayName}", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${spec.displayName}")
        info.addPara("Prevented some ships from acquiring additional d-mods.", 0f, Misc.getTextColor(), Misc.getHighlightColor())
    }

    override fun getTitleColor(mode: IntelInfoPlugin.ListInfoMode?): Color {
        return Misc.getBasePlayerColor()
    }

    override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        var tags = super.getIntelTags(map)
        tags.add("Skills")
        return tags
    }
}