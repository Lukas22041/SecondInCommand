package second_in_command.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipLocation
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.util.Misc
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.lazywizard.lazylib.MathUtils

var previouslyLoadedSprite = HashMap<String, Boolean>()

fun Float.levelBetween(min: Float, max: Float) : Float {
    var level = (this - min) / (max - min)
    level = MathUtils.clamp(level, 0f, 1f)
    return level
}

fun Any.logger() : Logger {
    return Global.getLogger(this::class.java).apply { level = Level.ALL }
}

fun SettingsAPI.getAndLoadSprite(filename: String) : SpriteAPI{
    if (!previouslyLoadedSprite.contains(filename)) {
        this.loadTexture(filename)
        previouslyLoadedSprite.put(filename, true)
    }
    return this.getSprite(filename)
}

fun SettingsAPI.loadTextureCached(filename: String){
    if (!previouslyLoadedSprite.contains(filename)) {
        this.loadTexture(filename)
        previouslyLoadedSprite.put(filename, true)
    }
}

fun TooltipMakerAPI.addPara(str: String) = this.addPara(str, 0f)

fun TooltipMakerAPI.addNegativePara(str: String) = this.addPara(str, 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())

fun TooltipMakerAPI.addTooltip(to: UIComponentAPI, location: TooltipLocation, width: Float, lambda: (TooltipMakerAPI) -> Unit) {
    this.addTooltipTo(object: TooltipCreator {
        override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
            return false
        }

        override fun getTooltipWidth(tooltipParam: Any?): Float {
            return width
        }

        override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
            lambda(tooltip!!)
        }

    }, to, location)
}

fun FleetMemberAPI.fixVariant() {
    if (this.variant.source != VariantSource.REFIT)
    {
        var variant = this.variant.clone();
        variant.originalVariant = null;
        variant.hullVariantId = Misc.genUID()
        variant.source = VariantSource.REFIT
        this.setVariant(variant, false, true)
    }
    this.updateStats()
}

fun ShipVariantAPI.baseOrModSpec() : ShipHullSpecAPI{
    if (this.hullSpec.baseHull != null) {
        return this.hullSpec.baseHull
    }
    return this.hullSpec
}

fun FleetMemberAPI.baseOrModSpec() : ShipHullSpecAPI{
    if (this.hullSpec.baseHull != null) {
        return this.hullSpec.baseHull
    }
    return this.hullSpec
}

fun ShipAPI.baseOrModSpec() : ShipHullSpecAPI{
    if (this.hullSpec.baseHull != null) {
        return this.hullSpec.baseHull
    }
    return this.hullSpec
}

public fun <T> MutableCollection<T>.randomAndRemove(): T {
    val pick = this.random()
    this.remove(pick)
    return pick
}

