package second_in_command.ui.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import second_in_command.misc.getAndLoadSprite
import second_in_command.misc.levelBetween
import java.awt.Color

class PlayerXPBarElement(tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    var player = Global.getSector().playerPerson
    var stats = player.stats
    var level = stats.level
    var xp = stats.xp
    var bonus = stats.bonusXp


    var plugin = Global.getSettings().levelupPlugin
    var storyPerLevel = plugin.storyPointsPerLevel
    var maxLevel = plugin.maxLevel

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

        renderXPBar(alphaMult)

        renderStoryBar(alphaMult)

        renderBorders(alphaMult)

        renderBonusLines(alphaMult)
    }

    fun renderBorders(alphaMult: Float) {
        borderSprite.angle = 0f
        borderSprite.alphaMult = alphaMult
        borderSprite.render(x-3, y  -2f)

        borderSprite.angle = 180f
        borderSprite.alphaMult = alphaMult
        borderSprite.render(width + x+2 - borderSprite.width, y -2f)
    }

    fun renderBonusLines(alphaMult: Float) {
        var count = storyPerLevel - 1
        var distancePerLine = width / storyPerLevel
        var distanceSoFar = 0f

        for (i in 0 until count) {
            distanceSoFar += distancePerLine

            var c = Color(20, 20, 20)
            //var c = borderColor
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)


            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)


            GL11.glColor4f(c.red / 255f,
                c.green / 255f,
                c.blue / 255f,
                c.alpha / 255f * (alphaMult))

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(x + distanceSoFar, y)
            GL11.glVertex2f(x + distanceSoFar, y + height)


            GL11.glEnd()
            GL11.glPopMatrix()
        }

    }

    fun getRequiredXP(curLevel: Int) : Float{
        /*var xp = 0f
        for (i in 1..(level+1)) {
            xp += plugin.getXPForLevel(level)
            var test = ""
        }
        return xp*/
        return plugin.getXPForLevel(curLevel).toFloat()
    }

    fun getXPLevel() : Float {
        return xp.toFloat().levelBetween(getRequiredXP(level), getRequiredXP(level+1))
    }

    fun getBonusXPLevel() : Float {

        var xp = getRequiredXP(level+1) - getRequiredXP(level)

        return bonus.toFloat().levelBetween(0f, xp)
    }

    fun renderXPBar(alphaMult: Float) {

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)


        var barLevel = getXPLevel()

        var curPos = y
        var segments = 30

        var progress = 0f
        var progressPerSegment = 1f * 2f / segments

        var swapped = false

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {

            var color1 = Color(0, 166, 198)
            var color2 = Color(0, 242, 255)
            var curColor = Misc.interpolateColor(color1, color2, progress)

            GL11.glColor4f(curColor.red / 255f,
                curColor.green / 255f,
                curColor.blue / 255f,
                curColor.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(x, curPos )
            GL11.glVertex2f(x+width * barLevel, curPos)
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

    fun renderStoryBar(alphaMult: Float) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        var bonusLevel = getBonusXPLevel()

        var barLevel = getXPLevel()

        //Needs to be multiplied by how much XP it doubles/quadruples, which is 4x as much at max level
        var mult = 2f
        if (level >= maxLevel) mult = 4f

        var barX = x+ width * barLevel
        var bonusWidth = (width * bonusLevel) * mult

        var maxWidth = width - (width * barLevel)
        bonusWidth = MathUtils.clamp(bonusWidth, 0f, maxWidth)

        var curPos = y
        var segments = 30

        var progress = 0f
        var progressPerSegment = 1f * 2f / segments

        var swapped = false

        GL11.glBegin(GL11.GL_QUAD_STRIP)

        for (segment in 0 .. segments)
        {

            var color1 = Color(109,154,113)
            var color2 = Color(178, 250, 157)
            var curColor = Misc.interpolateColor(color1, color2, progress)

            GL11.glColor4f(curColor.red / 255f,
                curColor.green / 255f,
                curColor.blue / 255f,
                curColor.alpha / 255f * (alphaMult * backgroundAlpha))

            GL11.glVertex2f(barX, curPos )
            GL11.glVertex2f(barX+bonusWidth, curPos)
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