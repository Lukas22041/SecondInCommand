package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import lunalib.lunaUI.elements.LunaElement
import org.dark.shaders.util.ShaderLib
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Vector3f
import second_in_command.misc.SCSettings
import java.awt.Color

class SkillWidgetElement(var id: String, var aptitudeId: String, var activated: Boolean, var canChangeState: Boolean, var preAcquired: Boolean, var iconPath: String, var soundId: String, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var sprite = Global.getSettings().getSprite(iconPath)
    var inactiveBorder = Global.getSettings().getSprite("graphics/secondInCommand/skillBorderInactive.png")
    var activeBorder = Global.getSettings().getSprite("graphics/secondInCommand/skillBorderActive.png")
    var eliteStars = Global.getSettings().getSprite("graphics/secondInCommand/elite_stars.png")
    var eliteBackground = Global.getSettings().getSprite("graphics/secondInCommand/elite_background.png")

    var hoverFade = 0f
    var time = 0f

    var isElite = false

    companion object {
        var shader = 0;
    }

    var glitchInterval = IntervalUtil(3f, 120f)
    var glitchDuration = -1f

    //var border = Global.getSettings().getSprite("test")

    init {
        enableTransparency = true
        backgroundAlpha = 0f
        borderAlpha = 0f

        if (aptitudeId == "rat_abyssal" && shader == 0) {
            shader = ShaderLib.loadShader(
                Global.getSettings().loadText("data/shaders/baseVertex.shader"),
                Global.getSettings().loadText("data/shaders/glitchFragmentAbyssal.shader"))
            if (shader != 0) {
                GL20.glUseProgram(shader)

                GL20.glUniform1i(GL20.glGetUniformLocation(shader, "tex"), 0)

                GL20.glUseProgram(0)
            } else {
                var test = ""
            }
        }

        onHoverEnter {
            playSound("ui_button_mouseover")
        }

        /*onClick {
            if (!activated) {
                playSound("leadership1")
            }
            else {
                playSound("ui_char_decrease_skill")
            }
            activated = !activated
        }*/
    }

    override fun advance(amount: Float) {
        super.advance(amount)

        time += 1 * amount

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0f, 1f)

        glitchInterval.advance(amount)
        if (glitchInterval.intervalElapsed()) {
            glitchDuration = MathUtils.getRandomNumberInRange(0.75f, 2f)
        }

        if (glitchDuration >= 0) {
            glitchDuration -= 1 * amount
        }

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

       /* var mult = 1f
        if (!activated) mult = 0.5f*/


        sprite.setNormalBlend()
        sprite.setSize(width-8, height-8)
        sprite.alphaMult = alphaMult

        //Contrast Mode
        if (SCSettings.highConstrastIcons!!) {
            if (!canChangeState && !activated) {
                sprite.color = Color(40, 40, 40)
            }
            else if (!activated) {
                sprite.color = Color(90, 90, 90)
            }
            else {
                sprite.color = Color(255, 255, 255)
            }
        } else {
            if (!canChangeState && !activated) {
                sprite.color = Color(50, 50, 50)
            }
            else if (!activated) {
                sprite.color = Color(140, 140, 140)
            }
            else {
                sprite.color = Color(255, 255, 255)
            }
        }


        sprite.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())


        if (isElite) {
            eliteBackground.setNormalBlend()
            eliteBackground.color = color.darker()
            eliteBackground.setSize(width, height)
            eliteBackground.alphaMult = alphaMult * 0.7f
            eliteBackground.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())
        }

        //Glitch Effect for Abyssal

        if (aptitudeId == "rat_abyssal" && activated && glitchDuration > 0 && shader != 0) {


            GL20.glUseProgram(shader)
            var mult = Vector3f(sprite.color.red / 255f, sprite.color.green / 255f, sprite.color.blue / 255f)
            GL20.glUniform3f(GL20.glGetUniformLocation(shader, "colorMult"), mult.x, mult.y, mult.z)
            GL20.glUniform1f(GL20.glGetUniformLocation(shader, "iTime"), time)
            GL20.glUniform1f(GL20.glGetUniformLocation(shader, "alphaMult"), alphaMult)

            //Bind Sprite
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.textureId)

            sprite.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

            GL20.glUseProgram(0)

        }


        if (activated) {

            activeBorder.setNormalBlend()
            activeBorder.color = color
            activeBorder.setSize(width, height)
            activeBorder.alphaMult = alphaMult
            activeBorder.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())


            activeBorder.setAdditiveBlend()
            activeBorder.color = color
            activeBorder.setSize(width, height)
            activeBorder.alphaMult = alphaMult * 0.2f
            activeBorder.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        }
        else {
            inactiveBorder.setNormalBlend()
            inactiveBorder.color = color
            if (SCSettings.highConstrastIcons!!) inactiveBorder.color = color.darker()
            inactiveBorder.setSize(width, height)
            inactiveBorder.alphaMult = alphaMult
            inactiveBorder.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

            inactiveBorder.setAdditiveBlend()
            inactiveBorder.color = color
            if (SCSettings.highConstrastIcons!!) inactiveBorder.color = color.darker()
            inactiveBorder.setSize(width, height)
            inactiveBorder.alphaMult = alphaMult * 0.2f
            inactiveBorder.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())
        }

        sprite.setAdditiveBlend()
        sprite.setSize(width-8, height-8)
        sprite.alphaMult = alphaMult * 0.5f * hoverFade
        sprite.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

        if (isElite) {


            eliteStars.setNormalBlend()
            //eliteStars.color = color
            eliteStars.setSize(width, height)
            eliteStars.alphaMult = alphaMult
            eliteStars.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt()+1f)
        }
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)

        var backgroundColor = Color(0, 0, 0)

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_CULL_FACE)


        if (alphaMult <= 0.8f) {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            GL11.glDisable(GL11.GL_BLEND)
        }





       // GL11.glDisable(GL11.GL_BLEND)

        GL11.glColor4f(backgroundColor.red / 255f,
            backgroundColor.green / 255f,
            backgroundColor.blue / 255f,
            backgroundColor.alpha / 255f * (alphaMult * backgroundAlpha))

        GL11.glRectf(x, y , x + width, y + height)

        GL11.glPopMatrix()
    }
}