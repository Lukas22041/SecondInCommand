package second_in_command.skills.automated

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.impl.campaign.ids.Skills
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.misc.randomAndRemove
import second_in_command.specs.SCBaseSkillPlugin

class WideRangeTransmission : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all fighters deployed from automated ships"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Deployed fighters now gain a copy of their mother-ships core", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - Those copies can hold at most 3 of the cores original skills", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "3")
        tooltip.addPara("   - These skills are randomly selected if there are more than 3", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "3")
        tooltip.addPara("   - Skills that are unique to a specific core can not be copied", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "")

    }

    override fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        if (Misc.isAutomated(ship)) {
            var core = ship!!.captain
            if (core == null || core.isDefault) return
            if (!core.isAICore) return

            var copy = Global.getFactory().createPerson()
            copy.name = core.name
            copy.portraitSprite = core.portraitSprite

            var skills = core.stats.skillsCopy
            var filtered = skills.filter { it.level > 0f && !it.skill.isAptitudeEffect && !it.skill.hasTag("npc_only") && !it.skill.hasTag("player_only") && !it.skill.hasTag("ai_core_only")}.toMutableList()
            var copySkills = ArrayList<String>()

            for (i in 0 until 3) {
                if (filtered.isEmpty()) continue
                var pick = filtered.randomAndRemove()
                copySkills.add(pick.skill.id)
            }

            for (skill in copySkills) {
                copy.stats.setSkillLevel(skill, 2f)
            }

            fighter!!.captain = copy

            if (copy.stats.hasSkill(Skills.MISSILE_SPECIALIZATION)) {
                for (weapon in fighter!!.allWeapons) {
                    if (weapon.type == WeaponAPI.WeaponType.MISSILE || weapon.type == WeaponAPI.WeaponType.COMPOSITE)
                        weapon.maxAmmo = fighter.mutableStats.missileAmmoBonus.computeEffective(weapon.spec.maxAmmo.toFloat()).toInt()
                    weapon.resetAmmo()
                }
            }
        }
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        if (Misc.isAutomated(stats)) {

        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (Misc.isAutomated(ship)) {

        }
    }


}