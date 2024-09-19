package second_in_command.skills.synchronised.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.util.Misc
import org.dark.shaders.post.PostProcessShader
import org.dark.shaders.util.ShaderLib
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.magiclib.kotlin.getDistance
import second_in_command.misc.baseOrModSpec
import second_in_command.misc.getAndLoadSprite
import java.awt.Color
import java.util.*
import kotlin.math.min

class SynchronisedScript : BaseCombatLayeredRenderingPlugin() {

    var vignette = Global.getSettings().getAndLoadSprite("graphics/fx/sc_vignette.png")

    var level = 0f
    var maxLevel = 1f

    var selectedShip: ShipAPI? = null
    var swapped = false

    companion object {
        var shader: Int = 0
    }

    override fun getActiveLayers(): EnumSet<CombatEngineLayers> {
        return EnumSet.of(CombatEngineLayers.JUST_BELOW_WIDGETS)
    }

    override fun getRenderRadius(): Float {
        return 10000000f
    }

    init {
       /* shader = ShaderLib.loadShader(
            Global.getSettings().loadText("data/shaders/baseVertex.shader"),
            Global.getSettings().loadText("data/shaders/glitchFragment.shader"))
        if (shader != 0) {
            GL20.glUseProgram(shader)

            GL20.glUniform1i(GL20.glGetUniformLocation(shader, "tex"), 0)
            GL20.glUniform1i(GL20.glGetUniformLocation(shader, "noiseTex1"), 1)
            GL20.glUniform1i(GL20.glGetUniformLocation(shader, "noiseTex2"), 2)

            GL20.glUseProgram(0)
        } else {
            var test = ""
        }*/

        shader = ShaderLib.loadShader(
            Global.getSettings().loadText("data/shaders/baseVertex.shader"),
            Global.getSettings().loadText("data/shaders/glitchFragment.shader"))
        if (shader != 0) {
            GL20.glUseProgram(shader)

            GL20.glUniform1i(GL20.glGetUniformLocation(shader, "tex"), 0)

            GL20.glUseProgram(0)
        } else {
            var test = ""
        }
    }


    override fun advance(amount: Float) {




        var engine = Global.getCombatEngine()

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !swapped) {
            level += 5f * amount
        } else {

            if (selectedShip != null && !swapped) {

                swapped = true

                Global.getSoundPlayer().playUISound("ui_neural_transfer_complete", 1f, 1f)
                Global.getCombatEngine().combatUI.reFanOutShipInfo()

                Global.getCombatEngine().setPlayerShipExternal(selectedShip)


            }

            level -= 5f * amount

            if (level <= 0f) swapped = false
            selectedShip = null

        }

        level = MathUtils.clamp(level, 0f, maxLevel)

        engine.timeMult.modifyMult("quickfire_slowdown", 1- (0.80f * level))


    }

    override fun render(layer: CombatEngineLayers?, viewport: ViewportAPI?) {

        var engine = Global.getCombatEngine()
        var playership = engine.playerShip ?: return


        //PostProcess
        PostProcessShader.setNoise(false, 0.5f * level)
        PostProcessShader.setSaturation(false, 1f + (0.3f * level))

        //Dont render anything if it wouldnt be visible anyways
        if (level <= 0f) return

        //Vignette
        vignette.color = Color(255, 255, 255)
        vignette.alphaMult = 0.2f * level


        var vignetteOffset = 0f
        vignette.setSize(viewport!!.visibleWidth + vignetteOffset, viewport!!.visibleHeight + vignetteOffset)
        vignette.render(viewport!!.llx - (vignetteOffset * 0.5f), viewport!!.lly - (vignetteOffset * 0.5f))



        var center = playership.location
        var ships = engine.ships.filter { it.isAlive }

        renderSelections(playership.spriteAPI, playership.location, Vector3f(1.5f, 0.75f, 0.5f), 0.25f * level)

        selectedShip = null
        if (ships.isNotEmpty()) {
            var viewMult = viewport!!.viewMult
            viewMult = viewMult.coerceIn(1.0f, 999f)

            var maxScale = 250f
            var radius = (25f + playership.shieldRadiusEvenIfNoShield + maxScale) /** viewMult*/

            var arcPer = 360 / ships.count()
            var currentArc = -30f

            for (ship in ships) {

                var sprite = Global.getSettings().getAndLoadSprite(ship.baseOrModSpec().spriteName)

                var widthScale = maxScale / sprite.width
                var heightScale = maxScale / sprite.height
                var scale = min(widthScale, heightScale)

                if (scale <= 1) {
                    sprite.setSize(sprite.width * scale, sprite.height * scale)
                }

                var loc = MathUtils.getPointOnCircumference(center, radius, currentArc)

                var vector = Vector3f(0.75f, 0.75f, 1.5f)
                var alpha = 0.85f * level
                if (playership.mouseTarget.getDistance(loc) <= maxScale * 0.8f) {
                    vector = Vector3f(1f, 1f, 1.75f)
                    alpha = 1f * level

                    if (ship != playership) {
                        selectedShip = ship
                    }
                }

                if (ship == playership) {
                    vector = Vector3f(1.5f, 0.5f, 0.5f)
                }

                renderSelections(sprite, loc, vector, alpha)




                //sprite.renderAtCenter(loc.x, loc.y)

                //Next loop
                currentArc += arcPer
            }
        }



    }

    fun renderSelections(sprite: SpriteAPI, loc: Vector2f, colorMult: Vector3f, alphaMult: Float) {
        var time = Global.getCombatEngine().getTotalElapsedTime(false) / 8



        GL20.glUseProgram(shader)


        GL20.glUniform3f(GL20.glGetUniformLocation(shader, "colorMult"), colorMult.x, colorMult.y, colorMult.z)
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "iTime"), time)
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "alphaMult"), alphaMult)

        //Bind Sprite
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.textureId)


        //Reset Texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0)

        sprite.setNormalBlend()
        sprite.renderAtCenter(loc.x, loc.y)

        GL20.glUseProgram(0)
    }


}