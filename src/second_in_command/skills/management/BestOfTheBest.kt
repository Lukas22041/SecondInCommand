package second_in_command.skills.management

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class BestOfTheBest : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Able to build 1 more permanent hullmod* in to ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - If this officer is unassigned, s-mods over the limit are made inactive", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")
        tooltip.addPara("   - Re-assigning this officer re-actives them", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")

        tooltip.addSpacer(10f)

        tooltip.addPara("*The base maximum number of permanent hullmods you're able to build into a ship is ${Misc.MAX_PERMA_MODS}." +
                "", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "${Misc.MAX_PERMA_MODS}")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.dynamic.getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("sc_best_of_the_best", 1f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun onActivation(data: SCData) {

        if (!data.isNPC) {
            for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
                var stats = member.stats
                var variant = member.variant

                stats!!.dynamic.getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("sc_best_of_the_best", 1f)


                for (tag in ArrayList(variant.tags)) {
                    if (tag.startsWith("sc_inactive_smods_")) {
                        var hmodId = tag.replace("sc_inactive_smods_", "")

                        variant.addPermaMod(hmodId, true)
                        variant.removeTag(tag)
                    }
                }

                variant.removePermaMod("sc_inactive_smods")
            }
        }


    }

    override fun onDeactivation(data: SCData) {

        if (!data.isNPC) {
            var base = Global.getSettings().getFloat("maxPermanentHullmods")

            for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
                var stats = member.stats
                stats!!.dynamic.getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).unmodify("sc_best_of_the_best")

                var maxSmods = stats.dynamic.getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).computeEffective(base)

                var variant = member.variant

                var any = false
                while (variant.sMods.count() > maxSmods) {
                    var last = variant.sMods.lastOrNull() ?: break
                    any = true
                    variant.removePermaMod(last)
                    variant.removeMod(last)

                    variant.addTag("sc_inactive_smods_$last")
                }

                if (any) {
                    variant.addPermaMod("sc_inactive_smods")
                }

            }
        }
    }
}