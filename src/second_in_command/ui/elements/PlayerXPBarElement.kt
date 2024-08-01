package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import org.magiclib.kotlin.setAlpha
import second_in_command.misc.getAndLoadSprite

class PlayerXPBarElement(tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var player = Global.getSector().playerPerson
    var stats = player.stats
    var level = stats.level
    var xp = stats.xp
    var bonus = stats.bonusXp


    var plugin = Global.getSettings().levelupPlugin
    var storyPerLevel = plugin.storyPointsPerLevel
    var requiredXp = plugin.getXPForLevel(level)

    var borderSprite = Global.getSettings().getAndLoadSprite("graphics/secondInCommand/xpBarBorder.png")

    init {

        renderBackground = false
        renderBorder = false
        enableTransparency = true
        borderAlpha = 0.4f

    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

        renderBackground(alphaMult)




        renderBorders(alphaMult)
    }

    fun renderBorders(alphaMult: Float) {
        borderSprite.angle = 0f
        borderSprite.alphaMult = alphaMult
        borderSprite.render(x-3, y  -2f)

        borderSprite.angle = 180f
        borderSprite.alphaMult = alphaMult
        borderSprite.render(width + x+2 - borderSprite.width, y -2f)
    }

    fun renderBackground(alphaMult: Float) {

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        var curPos = y
        var segments = 30

        var progress = 0f
        var progressPerSegment = 1f * 2f / segments

        var swapped = false

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {

            var curColor = Misc.interpolateColor(Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor().darker(), progress)

            GL11.glColor4f(curColor.red / 255f,
                curColor.green / 255f,
                curColor.blue / 255f,
                curColor.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(x, curPos )
            GL11.glVertex2f(x+width, curPos)
            curPos += height / segments

            if (!swapped) {
                progress += progressPerSegment
            }
            else {
                progress -= progressPerSegment
            }

            if (progress >= 1) {
                swapped = true
            }
        }

        GL11.glEnd()
        GL11.glPopMatrix()

    }

}