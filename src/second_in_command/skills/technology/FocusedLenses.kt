package second_in_command.skills.technology

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class FocusedLenses : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("20%% of shield damage dealt by beams is converted into hard-flux", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())


    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {



    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

        if (!ship!!.hasListenerOfClass(FocusedLensesListener::class.java)) {
            ship.addListener(FocusedLensesListener())
        }

    }



}

class FocusedLensesListener : DamageDealtModifier {

    override fun modifyDamageDealt(param: Any?, target: CombatEntityAPI?, damage: DamageAPI?, point: Vector2f?, shieldHit: Boolean): String? {
        if (!shieldHit) return null
        if (param !is BeamAPI) return null
        if (target !is ShipAPI) return null
        if (damage!!.isForceHardFlux) return null

        var dps = damage!!.dpsDuration
        if (dps <= 0) return null

        //Apply extra damage
        val dam = (damage.damage * damage.dpsDuration) * 0.2f
        Global.getCombatEngine()!!.applyDamage(target, point, dam, damage.type, 0f, false, false, param.source)

        //Reduce damage dealt
        damage.modifier.modifyMult("sc_focused_lens", 0.8f)

        return "sc_focused_lens"
    }
}