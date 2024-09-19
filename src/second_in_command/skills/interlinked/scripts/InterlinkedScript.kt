package second_in_command.skills.interlinked.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.input.InputEventAPI
import org.dark.shaders.util.ShaderLib
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Vector2f
import second_in_command.misc.baseOrModSpec
import second_in_command.misc.getAndLoadSprite
import java.util.*
import kotlin.math.max
import kotlin.math.min

class InterlinkedScript : BaseCombatLayeredRenderingPlugin() {


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

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            engine.timeMult.modifyMult("quickfire_slowdown", 0.20f)
        } else {
            engine.timeMult.unmodify("quickfire_slowdown")
        }

    }

    override fun render(layer: CombatEngineLayers?, viewport: ViewportAPI?) {

        var engine = Global.getCombatEngine()
        var playership = engine.playerShip ?: return

        var center = playership.location
        var ships = engine.ships.filter { it.isAlive }

        var viewMult = viewport!!.viewMult
        viewMult = viewMult.coerceIn(1.0f, 999f)

        var maxScale = 150
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


            renderSelections(sprite, loc)


            //sprite.renderAtCenter(loc.x, loc.y)

            //Next loop
            currentArc += arcPer
        }

    }

    fun renderSelections(sprite: SpriteAPI, loc: Vector2f) {
        var time = Global.getCombatEngine().getTotalElapsedTime(false) / 8



        GL20.glUseProgram(shader)


        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "iTime"), time)
        GL20.glUniform1f(GL20.glGetUniformLocation(shader, "alphaMult"), 0.85f)

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