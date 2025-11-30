package second_in_command.skills.scavenging.entities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignEngineLayers
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetAssignment
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel
import com.fs.starfarer.api.combat.ViewportAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin
import com.fs.starfarer.api.impl.campaign.SensorArrayEntityPlugin
import com.fs.starfarer.api.impl.campaign.ghosts.BaseSensorGhost
import com.fs.starfarer.api.impl.campaign.ghosts.GBGoInDirection
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.ids.Pings
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.util.vector.Vector2f
import org.magiclib.kotlin.fadeAndExpire
import java.awt.Color
import java.util.*
import kotlin.math.sign

class ScrapforgeDeployable : BaseCustomEntityPlugin() {

    var GLOW_FREQUENCY = 1f

    @Transient
    private var sprite: SpriteAPI? = null

    @Transient
    private var glow: SpriteAPI? = null

    var addedIntel = false

    enum class Type {
        DETECTOR, DISRUPTOR
    }

    var type = Type.DETECTOR
    var rotation = MathUtils.getRandomNumberInRange(0f, 360f)
    var timeToLife = 30f

    override fun init(entity: SectorEntityToken, pluginParams: Any?) {
        super.init(entity, pluginParams)
        //this.entity = entity;
        readResolve()
    }

    fun readResolve(): Any {
        sprite = Global.getSettings().getSprite("campaignEntities", "warning_beacon")
        glow = Global.getSettings().getSprite("campaignEntities", "warning_beacon_glow")
        return this
    }

    fun getColor() : Color {
        var color = Color.white
        if (type == Type.DETECTOR) color = Color(225, 50, 100)
        if (type == Type.DISRUPTOR) color = Color(0, 150, 250)
        return color
    }

    private var phase = 0f
    private var freqMult = 1f
    private var sincePing = 10f
    override fun advance(amount: Float) {

        rotation += 1 * amount
        if (rotation >= 360) {
            rotation = 0f
        }
        entity.facing = rotation

        var days = Global.getSector().clock.convertToDays(amount)
        timeToLife -= 1f * days
        if (timeToLife <= 0 && !entity.isExpired) {
            entity.fadeAndExpire(1f)
        }


        if (type == Type.DETECTOR) {
            handleDetectorLogic(amount)
        } else if (type == Type.DISRUPTOR) {
            handleDisruptorLogic(amount)
        }

        phase += amount * GLOW_FREQUENCY * freqMult
        while (phase > 1) phase--

        if (entity.isInCurrentLocation) {
            sincePing += amount
            if (sincePing >= 6f && phase > 0.1f && phase < 0.2f) {
                sincePing = 0f
                val playerFleet = Global.getSector().playerFleet
                if (playerFleet != null && entity.getVisibilityLevelTo(playerFleet) == SectorEntityToken.VisibilityLevel.COMPOSITION_AND_FACTION_DETAILS) {
                    var pingId: String? = Pings.WARNING_BEACON1
                    freqMult = 1f

                    var pingColor = getColor()
                    Global.getSector().addPing(entity, pingId, pingColor)
                }
            }
        }
    }

    var icons = ArrayList<SectorEntityToken>()



    fun handleDetectorLogic(amount: Float) {

        for (other in ArrayList(entity.starSystem.allEntities)) {
            //if (entity.isVisibleToPlayerFleet == false) {
            if (other.hasTag("sc_is_scrap_icon")) continue
            if (other.hasTag("sc_has_scrapforge_icon")) continue

          /*  if (!other.isDiscoverable && other !is CampaignFleetAPI) continue
            //if (other.isVisibleToSensorsOf(Global.getSector().playerFleet)) continue
            if (Global.getSector().playerFleet.getVisibilityLevelTo(other) != VisibilityLevel.NONE) continue*/

            var icon: SectorEntityToken? = null

            if (other is CampaignFleetAPI) {
                if (Global.getSector().playerFleet.getVisibilityLevelTo(other) != VisibilityLevel.NONE) continue
                icon = other.starSystem.addCustomEntity("sc_scrapforge_deployable_fleet_icon_${Misc.genUID()}", "Unknown Fleet", "sc_scrapforge_deployable_fleet_icon", Factions.NEUTRAL)
            } else {
                if (!other.isDiscoverable) continue
                if (Global.getSector().playerFleet.getVisibilityLevelTo(other) != VisibilityLevel.NONE) continue

                icon = other.starSystem.addCustomEntity("sc_scrapforge_deployable_non_fleet_icon_${Misc.genUID()}", "Unknown Entity", "sc_scrapforge_deployable_non_fleet_icon", Factions.NEUTRAL)
            }

            if (icon != null) {
                other.addTag("sc_has_scrapforge_icon")
                icon.setCircularOrbit(other, 0f, 0.1f, 300f)
                icon.addTag("sc_is_scrap_icon")
                icons.add(icon)
            }

        }

        for (icon in ArrayList(icons)) {
            var orbitFocus = icon.orbitFocus

            var shouldRemove = false
            if (this.entity.isExpired) shouldRemove = true
            if (orbitFocus == null) shouldRemove = true

            if (orbitFocus != null) {
                if (orbitFocus.isExpired) shouldRemove = true
                if (!orbitFocus.isAlive) shouldRemove = true

                if (orbitFocus is CampaignFleetAPI) {
                    if (Global.getSector().playerFleet.getVisibilityLevelTo(orbitFocus) != VisibilityLevel.NONE) shouldRemove = true
                } else {
                    if (!orbitFocus.isDiscoverable) shouldRemove = true
                }

              /*  if (orbitFocus.isDiscoverable) shouldRemove = true
                if (orbitFocus.isVisibleToPlayerFleet) shouldRemove = true*/
            }

            if (shouldRemove) {
                icon.fadeAndExpire(0f)
                if (orbitFocus != null) orbitFocus.removeTag("sc_has_scrapforge_icon")
                icons.remove(icon)
            }
        }
    }





    fun handleDisruptorLogic(amount: Float) {

        for (fleet in entity.containingLocation.fleets) {
            if (fleet.isInHyperspaceTransition) continue

            if (!fleet.faction.isPlayerFaction) {
                respondToFalseSensorReadings(fleet)
            }
        }
    }

    var GHOST_RESPONSE: String = "ghost_response" // custom value added to assignments so we know which to clear


    fun respondToFalseSensorReadings(fleet: CampaignFleetAPI) {
        if (fleet.isStationMode) return
        if (fleet.ai == null) {
            return
        }
        if (fleet.ai.assignmentsCopy == null) {
            return
        }

        val mem = fleet.memoryWithoutUpdate
        if (mem.getBoolean(MemFlags.FLEET_NOT_CHASING_GHOST)) {
            return
        }
        if (mem.getBoolean(MemFlags.FLEET_CHASING_GHOST)) {
            return
        }
        if (mem.getBoolean(MemFlags.FLEET_BUSY)) {
            return
        }
        val patrol = mem.getBoolean(MemFlags.MEMORY_KEY_PATROL_FLEET)
        val warFleet = mem.getBoolean(MemFlags.MEMORY_KEY_WAR_FLEET)
        val pirate = mem.getBoolean(MemFlags.MEMORY_KEY_PIRATE)
        if (!patrol && !warFleet && !pirate) {
            return
        }

        var random: Random? = mem[MemFlags.FLEET_CHASING_GHOST_RANDOM] as Random?
        if (random == null) {
            random = Misc.getRandom(Misc.getSalvageSeed(fleet), 7)
            mem.set(MemFlags.FLEET_CHASING_GHOST_RANDOM, random, 30f)
        }

        val willRespond = random!!.nextFloat() < 0.85f
        if (!willRespond) {
            mem.set(MemFlags.FLEET_NOT_CHASING_GHOST, true, 0.25f * random.nextFloat())
            Misc.setFlagWithReason(fleet.memoryWithoutUpdate, MemFlags.FLEET_CHASING_GHOST, GHOST_RESPONSE, false, 0f)
            for (curr in fleet.ai.assignmentsCopy) {
                if (GHOST_RESPONSE == curr.custom) {
                    fleet.ai.removeAssignment(curr)
                }
            }
            return
        }
        val chaseDur = (3f + Math.random().toFloat()) * 2f
        Misc.setFlagWithReason(fleet.memoryWithoutUpdate, MemFlags.FLEET_CHASING_GHOST, GHOST_RESPONSE, true, chaseDur)
        mem[MemFlags.FLEET_BUSY, true] = chaseDur
        mem[MemFlags.FLEET_NOT_CHASING_GHOST, true] = chaseDur + 6f + 3f * random!!.nextFloat()

        var angle = Misc.getAngleInDegrees(fleet.location) // away from center of system;
        val arc = 270f
        angle += arc / 2f - arc * random!!.nextFloat()
        val dist = 3000f + 3000f * random!!.nextFloat()
        val loc = Misc.getUnitVectorAtDegreeAngle(angle)
        loc.scale(dist)
        Vector2f.add(loc, fleet.location, loc)

        val actionText = "investigating anomalous sensor reading"

        val target = fleet.containingLocation.createToken(loc)
        fleet.addAssignmentAtStart(FleetAssignment.PATROL_SYSTEM, target, 3f, actionText, null)
        var curr = fleet.currentAssignment
        if (curr != null) {
            curr.custom = GHOST_RESPONSE
        }

        if (dist > 2000f) {
            fleet.addAssignmentAtStart(FleetAssignment.GO_TO_LOCATION, target, 3f, actionText, null)
            curr = fleet.currentAssignment
            if (curr != null) {
                curr.custom = GHOST_RESPONSE
            }
        }
    }

    override fun getRenderRange(): Float {
        return entity.radius + 100f
    }

    override fun render(layer: CampaignEngineLayers?, viewport: ViewportAPI) {
        var alphaMult = viewport.alphaMult
        alphaMult *= entity.sensorFaderBrightness
        alphaMult *= entity.sensorContactFaderBrightness
        if (alphaMult <= 0f) return
        val spec = entity.customEntitySpec ?: return
        val w = spec.spriteWidth
        val h = spec.spriteHeight
        val loc = entity.location
        sprite!!.angle = entity.facing - 90f
        sprite!!.setSize(w, h)
        sprite!!.alphaMult = alphaMult
        sprite!!.setNormalBlend()
        sprite!!.renderAtCenter(loc.x, loc.y)
        var glowAlpha = 0f
        if (phase < 0.5f) glowAlpha = phase * 2f
        if (phase >= 0.5f) glowAlpha = 1f - (phase - 0.5f) * 2f
        val glowAngle1 = (phase * 1.3f % 1 - 0.5f) * 12f
        val glowAngle2 = (phase * 1.9f % 1 - 0.5f) * 12f

        var glowColor = getColor()

        glow!!.color = glowColor
        glow!!.setSize(w, h)
        glow!!.alphaMult = alphaMult * glowAlpha
        glow!!.setAdditiveBlend()
        glow!!.angle = entity.facing - 90f + glowAngle1
        glow!!.renderAtCenter(loc.x, loc.y)
        glow!!.angle = entity.facing - 90f + glowAngle2
        glow!!.alphaMult = alphaMult * glowAlpha * 0.5f
        glow!!.renderAtCenter(loc.x, loc.y)
    }

    override fun createMapTooltip(tooltip: TooltipMakerAPI, expanded: Boolean) {
        val color = entity.faction.baseUIColor
        var postColor = color
        tooltip.addPara(entity.name, 0f, color, postColor, "")
    }

    override fun hasCustomMapTooltip(): Boolean {
        return true
    }

    override fun appendToCampaignTooltip(tooltip: TooltipMakerAPI, level: SectorEntityToken.VisibilityLevel?) {

    }
}