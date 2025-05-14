package second_in_command.skills.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.characters.*
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Strings
import com.fs.starfarer.api.impl.hullmods.Automated
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.misc.baseOrModSpec
import second_in_command.scripts.AutomatedShipsManager
import second_in_command.skills.automated.SCBaseAutoPointsSkillPlugin

class UtilitySkill : SCBaseVanillaShipSkill() {
    override fun getScopeDescription(): LevelBasedEffect.ScopeDescription {
        return LevelBasedEffect.ScopeDescription.PILOTED_SHIP
    }

    override fun createCustomDescription(stats: MutableCharacterStatsAPI?, skill: SkillSpecAPI?, info: TooltipMakerAPI?, width: Float) {


    }

    override fun apply(stats: MutableShipStatsAPI?, hullSize: ShipAPI.HullSize?, id: String?, level: Float) {
        if (Global.getSector()?.playerFleet?.fleetData != null) {
            for (skill in SCUtils.getPlayerData().getAllActiveSkillsPlugins()) {
                skill.callEffectsFromSeparateSkill(stats, hullSize, "${skill.id}_$id")
            }
        }
    }

    override fun unapply(stats: MutableShipStatsAPI?, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    class AutomatedItem() : SCPlaceholderShipSkill(), FleetTotalSource {

        data class DPData(var member: FleetMemberAPI, var points: Int, var mult: Float)

        override fun getFleetTotalItem(): FleetTotalItem? {
            var fleet = Global.getSector()?.playerFleet ?: return FleetTotalItem()
            var manager = AutomatedShipsManager.get() ?: return FleetTotalItem()
            var data = SCUtils.getPlayerData()

            var used = manager.getUsedDP().toInt()
            var max = manager.getMaximumDP().toInt()

            var value = "Inactive"
            if (max != 0) value = "$used (max $max)"

            val item = FleetTotalItem()
            item.label = "Automated ships"
            if (max == 0) item.valueColor = Misc.getGrayColor()
            item.value = value
            item.sortOrder = 350f

            item.tooltipCreator = object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any?): Float {
                    return 450f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {




                    tooltip!!.addPara("Automated ships require certain skills to be usable and recoverable. " +
                            "If active, displays the total deployment points of all automated ships in your fleet and the maximum budget provided by active skills. ",
                        0f)

                    tooltip.addSpacer(10f)


                    //Show relevant skills here

                    var autoSkills = data.getAllActiveSkillsPlugins().filter { it is SCBaseAutoPointsSkillPlugin } as List<SCBaseAutoPointsSkillPlugin>
                    var extra = ""
                    if (autoSkills.isNotEmpty()) extra = "The following skills expand your budget of automated points:"

                    tooltip.addPara("Automated ships have their base combat readiness penalty negated by skills that provide automated points. " +
                            "Using less or equal to 100%% of the available budget will negate 100%% of the penalty. Using twice of what your budget offers negates only 50%% of the penalty. $extra",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "100%", "100%", "50%")


                    var stat = Global.getSector().playerPerson.stats.dynamic.getMod("sc_auto_dp")
                    var bonuses = stat.flatBonuses

                    if (bonuses.isNotEmpty()) tooltip.addSpacer(10f)




                    var entries = HashMap<String, Int>()

                    for (skill in autoSkills.sortedByDescending { it.getProvidedPoints() }) {
                        var points = skill.getProvidedPoints()

                        var autoString = "   +$points"
                        autoString += "   ${skill.name} Skill"

                        entries.put(autoString, points)

                        //tooltip.addPara(autoString, 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+$points")
                    }


                    for (bonus in bonuses) {
                        if (!bonus.key.contains("_external")) continue
                        var points = bonus.value.value.toInt()

                        var autoString = "   +$points"
                        autoString += "   ${bonus.value.desc}"

                        entries.put(autoString, points)

                        //tooltip.addPara(autoString, 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+$points")
                    }

                    for (entry in entries.toList().sortedByDescending { it.second }) {
                        tooltip.addPara(entry.first, 0f, Misc.getTextColor(), Misc.getHighlightColor(), "+${entry.second}")
                    }


                    tooltip.addSpacer(10f)

                    tooltip.addPara("The following ships contribute to the total amount of the used budget. Mothballed ships are not counted towards the total. AI Cores and other factors can apply a multiplier to the deployment point value used. ",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "AI Cores", "other factors")

                    tooltip.addSpacer(10f)

                    //Auto points

                    var autoShips = ArrayList<DPData>()

                    for (curr in fleet.fleetData.membersListCopy) {
                        if (curr.isMothballed) continue
                        if (!Misc.isAutomated(curr)) continue
                        if (Automated.isAutomatedNoPenalty(curr)) continue
                        var mult = 1f
                        var points = curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_VALUE)
                        mult = curr.captain.memoryWithoutUpdate.getFloat(AICoreOfficerPlugin.AUTOMATED_POINTS_MULT)

                        if (mult == 0f) mult = 1f

                        var memberMult = curr.stats.dynamic.getStat("sc_auto_points_mult").modifiedValue
                        mult *= memberMult

                        points += Math.round(curr.deploymentPointsCost * mult).toFloat()

                        var data = DPData(curr, points.toInt(), mult)
                        autoShips.add(data)
                    }

                    var maxEntries = 15
                    var entryCount = 0

                    for (autoData in ArrayList(autoShips.sortedByDescending { it.points })) {

                        entryCount++
                        if (entryCount >= maxEntries) break

                        autoShips.remove(autoData)

                        var points = autoData.points
                        var mult = autoData.mult
                        var member = autoData.member

                        var multString = String.format("%.2f", mult)

                        tooltip.addPara("   +$points   ${member.shipName}, ${member.baseOrModSpec().hullName}-class (${multString}${Strings.X})",
                            0f, Misc.getTextColor(), Misc.getHighlightColor(), "+$points", "${multString}${Strings.X}")
                    }

                    //Prevent displaying way to many ships if there are tons of automated ships
                    if (autoShips.isNotEmpty()) {
                        var points = autoShips.sumOf { it.points }
                        tooltip.addPara("   +$points points from other ships", 0f,
                            Misc.getTextColor(), Misc.getHighlightColor(), "+$points")
                    }

                }

            }

            return item
        }
    }

}