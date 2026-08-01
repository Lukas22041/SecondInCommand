package second_in_command.patches

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.StatBonus
import com.fs.starfarer.campaign.ui.trade.CargoItemStack
import patchlib.api.context.AfterContext
import patchlib.api.match.ClassMatch
import patchlib.api.match.MethodMatch
import patchlib.api.patch.After
import patchlib.api.patch.Patch

@Patch(target = ClassMatch(CargoItemStack::class))
object SCCargoItemStackPatch {

    /** Mult & percent only, flat mods wont work well with this */
    val WEAPON_STORAGE_MOD_KEY = "sic_weapon_storage_mod";

    @JvmStatic
    @After(target = MethodMatch(methodName = "getCargoSpace"))
    public fun afterGetCargoSpace(context: AfterContext) {
        var stack = context.getInferredSelf<CargoItemStack>();
        val base = context.getInferredReturnValue<Float>()

        val overwrite = modifyCargoSpace(stack, base)
        if (overwrite != null) context.setReturnValue(overwrite);
    }

    @JvmStatic
    @After(target = MethodMatch(methodName = "getCargoSpacePerUnit"))
    public fun afterGetCargoSpacePerUnit(context: AfterContext) {
        var stack = context.getInferredSelf<CargoItemStack>();
        val base = context.getInferredReturnValue<Float>()

        val overwrite = modifyCargoSpace(stack, base)
        if (overwrite != null) context.setReturnValue(overwrite);
    }

    private fun modifyCargoSpace(stack: CargoItemStack, base: Float) : Float? {
        if (!stack.isWeaponStack) return null;
        if (Global.getSector().playerFleet == null) return null
        if (!stack.isInPlayerCargo) return null
        val player = Global.getSector().playerPerson ?: return null;
        val bonus = player.stats.dynamic.getMod(WEAPON_STORAGE_MOD_KEY)
        return Math.max(0f, bonus.computeEffective(base))
    }

}