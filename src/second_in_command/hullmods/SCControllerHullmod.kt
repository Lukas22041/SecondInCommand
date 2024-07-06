package second_in_command.hullmods

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.misc.baseOrModSpec

class SCControllerHullmod : BaseHullMod() {

    companion object {
        fun ensureAddedControllerToFleet() {
            var playerfleet = Global.getSector().playerFleet ?: return
            if (playerfleet.fleetData?.membersListCopy == null) return
            for (member in playerfleet.fleetData.membersListCopy) {
                if (!member.variant.hasHullMod("sc_skill_controller")) {
                    if (member.variant.source != VariantSource.REFIT) {
                        var variant = member.variant.clone();
                        variant.originalVariant = null;
                        variant.hullVariantId = Misc.genUID()
                        variant.source = VariantSource.REFIT
                        member.setVariant(variant, false, true)
                    }

                    member.variant.addMod("sc_skill_controller")

                    var moduleSlots = member.variant.moduleSlots
                    for (slot in moduleSlots) {
                        var module = member.variant.getModuleVariant(slot)
                        module.addMod("sc_skill_controller")
                    }
                }
            }
        }
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, id: String?) {

        var skills = SCUtils.getSCData().getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsAfterShipCreation(ship, ship!!.variant, "${id}_${skill.getId()}")
        }
    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        var skills = SCUtils.getSCData().getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.applyEffectsBeforeShipCreation(stats, stats!!.variant, hullSize, "${id}_${skill.getId()}")
        }
    }

    override fun advanceInCampaign(member: FleetMemberAPI?, amount: Float) {
        var skills = SCUtils.getSCData().getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCampaign(member, amount)
        }
    }

    override fun advanceInCombat(ship: ShipAPI?, amount: Float) {
        var skills = SCUtils.getSCData().getAllActiveSkillsPlugins()
        for (skill in skills) {
            skill.advanceInCombat(ship, amount)
        }
    }
}