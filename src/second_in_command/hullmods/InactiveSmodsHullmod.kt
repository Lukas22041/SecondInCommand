package second_in_command.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

class InactiveSmodsHullmod : BaseHullMod() {

    override fun shouldAddDescriptionToTooltip(hullSize: ShipAPI.HullSize?, ship: ShipAPI?, isForModSpec: Boolean): Boolean {
        return false
    }

    override fun addPostDescriptionSection(tooltip: TooltipMakerAPI?, hullSize: ShipAPI.HullSize?, ship: ShipAPI?, width: Float, isForModSpec: Boolean) {

        tooltip!!.addSpacer(10f)
        tooltip.addPara("The ship has inactive s-mods due to exceeding the maximum count of them after the \"Best of the Best\" skill was unassigned. " +
                "Re-assign an officer with the skill to unlock them again.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "inactive s-mods", "Best of the Best")
        tooltip!!.addSpacer(10f)

        tooltip.addPara("The inactive hullmods are:" ,0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        var variant = ship!!.variant

        for (tag in ArrayList(variant.tags)) {
            if (tag.startsWith("sc_inactive_smods_")) {
                var hmodId = tag.replace("sc_inactive_smods_", "")

                var spec = Global.getSettings().getHullModSpec(hmodId)
                tooltip.addPara(" - ${spec.displayName}", 0f)

            }
        }
    }

}