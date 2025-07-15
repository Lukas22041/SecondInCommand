package second_in_command.skills.starfaring

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BaseCampaignEventListener
import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.impl.campaign.skills.FieldRepairsScript
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.WeightedRandomPicker
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.misc.baseOrModSpec
import second_in_command.specs.SCBaseSkillPlugin
import java.awt.Color

class ContinuousRepairs : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Ships lost in combat have a 60/60/40/30 percent chance to avoid d-mods, based on hullsize", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Every 240 deployment points worth of opponents defeated remove a random d-mod from a random ship", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Defeated capital ships provide twice as much towards this score", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "capital ships", "twice")
        tooltip.addPara("   - This effect can trigger multiple times from the same battle", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - This count is being kept track of between battles", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Ignores ships with the Rugged Construction hullmod", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Rugged Construction")

    }

   /* override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        when (hullSize) {
            ShipAPI.HullSize.FRIGATE -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0f)
            ShipAPI.HullSize.DESTROYER -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0f)
            ShipAPI.HullSize.CRUISER -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0f)
            ShipAPI.HullSize.CAPITAL_SHIP -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0f)
        }

    }*/

    override fun callEffectsFromSeparateSkill(stats: MutableShipStatsAPI?, hullSize: ShipAPI.HullSize?, id: String?) {
        when (hullSize) {
            ShipAPI.HullSize.FRIGATE -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.4f)
            ShipAPI.HullSize.DESTROYER -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.4f)
            ShipAPI.HullSize.CRUISER -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.6f)
            ShipAPI.HullSize.CAPITAL_SHIP -> stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.7f)
            else -> null
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

    override fun advance(data: SCData, amount: Float) {

    }

    override fun onActivation(data: SCData) {
        if (data.isPlayer) {
            if (Global.getSector().allListeners.none { it is ContinuousRepairsListener }) {
                Global.getSector().addListener(ContinuousRepairsListener())
            }
        }
    }

    override fun onDeactivation(data: SCData) {
        if (data.isPlayer) {
            var listeners = Global.getSector().allListeners.filter { it is ContinuousRepairsListener }
            for (listener in ArrayList(listeners)) {
                Global.getSector().removeListener(listener)
            }
        }
    }

}


class ContinousIntel(var pick: FleetMemberAPI, var specId: String) : BaseIntelPlugin() {

    init {
        Global.getSector().addScript(this)
        endAfterDelay(14f)
    }

    override fun notifyEnded() {
        Global.getSector().removeScript(this)
    }

    override fun getName(): String {
        return "Skill - Continuous Repairs"
    }

    override fun getIcon(): String {
        return "graphics/secondInCommand/starfaring/continuous_repairs.png"
    }

    override fun hasSmallDescription(): Boolean {
        return false
    }

    override fun addBulletPoints(info: TooltipMakerAPI?, mode: IntelInfoPlugin.ListInfoMode?, isUpdate: Boolean, tc: Color?, initPad: Float) {
        var spec = Global.getSettings().getHullModSpec(specId)
        info!!.addPara("${pick.shipName} - removed ${spec.displayName}", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "${spec.displayName}")
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

class ContinuousRepairsListener() : BaseCampaignEventListener(false) {

    var required = 240

    override fun reportEncounterLootGenerated(plugin: FleetEncounterContextPlugin?, loot: CargoAPI?) {
        if (plugin == null) return

        if (plugin.battle.isPlayerSide(plugin.battle.getSideFor(plugin.winner))) {
            var dp = SCUtils.getSectorData().continiousRepairsDPSoFar

            for (data in plugin.loserData.ownCasualties) {
                dp += data.member.deploymentPointsCost
                if (data.member.isCapital) dp += data.member.deploymentPointsCost
            }

            while (dp >= required) {
                dp -= required

                var picks = WeightedRandomPicker<FleetMemberAPI>()
                for (member in Global.getSector().playerFleet.fleetData.membersListCopy) {
                    if (member.variant.hasDMods() && !member.variant.hasHullMod("rugged") && !member.baseOrModSpec().hasTag(Tags.HULL_UNRESTORABLE) && !member.variant.hasTag(Tags.VARIANT_UNRESTORABLE)) {
                        picks.add(member)
                    }
                }

                var pick = picks.pick()
                if (pick != null) {

                    var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }

                    var hmods = pick.variant.permaMods + pick.variant.hullMods

                    var foundDmods = ArrayList<String>()
                    for (hmod in hmods) {
                        if (dmodSpecs.map { it.id }.contains(hmod)) {
                           foundDmods.add(hmod)
                        }
                    }

                    var hmodPick = foundDmods.randomOrNull()
                    if (hmodPick != null && pick.variant != null) {
                        DModManager.removeDMod(pick.variant, hmodPick)

                        val spec = DModManager.getMod(hmodPick)
                        /*val intel = MessageIntel(pick.shipName + " - repaired " + spec.displayName,
                            Misc.getBasePlayerColor())
                        intel.icon = Global.getSettings().getSpriteName("intel", "repairs_finished")
                        Global.getSector().campaignUI.addMessage(intel, MessageClickAction.REFIT_TAB, pick)*/


                        //Intel
                        var intel = ContinousIntel(pick, spec.id)

                        Global.getSector().intelManager.addIntel(intel)

                        //Restore Visuals
                        val remainingdmods = DModManager.getNumDMods(pick.variant)
                        if (remainingdmods <= 0) {
                            FieldRepairsScript.restoreToNonDHull(pick.variant)
                        }
                    }
                }
            }

            SCUtils.getSectorData().continiousRepairsDPSoFar = dp
        }

    }


}