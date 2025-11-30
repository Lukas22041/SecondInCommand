package second_in_command.skills.scavenging.entities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.fadeAndExpire

class ScrapforgeEntityIcon : BaseCustomEntityPlugin() {

//    var source: SectorEntityToken? = null


   /* override fun advance(amount: Float) {
        super.advance(amount)

        if (source == null) {
            entity.fadeAndExpire(0f)
            if (entity.orbitFocus != null) entity.orbitFocus.removeTag("sc_has_scrapforge_icon")
            return
        }

        if (source!!.isExpired) {
            entity.fadeAndExpire(0f)
            if (entity.orbitFocus != null) entity.orbitFocus.removeTag("sc_has_scrapforge_icon")
            return
        }

        if (entity.orbitFocus != null)
        {
            if (entity.containingLocation != entity.orbitFocus.containingLocation)
            {
                entity.fadeAndExpire(0f)
                entity.orbitFocus.removeTag("sc_has_scrapforge_icon")
            }
        }

        if (entity.orbitFocus != null)
        {
            if (entity.orbitFocus.isVisibleToPlayerFleet)
            {
                entity.fadeAndExpire(0f)
                entity.orbitFocus.removeTag("sc_has_scrapforge_icon")
            }
        }
    }*/
}