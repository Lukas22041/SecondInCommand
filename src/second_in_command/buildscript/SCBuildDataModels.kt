package second_in_command.buildscript

import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color

// Top-level export structure
data class BuildExportData(
    val version: String,
    val exportDate: String,
    val tooltipWidth: Float,
    val aptitudes: List<AptitudeData>
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("version", version)
        obj.put("exportDate", exportDate)
        obj.put("tooltipWidth", tooltipWidth.toDouble())
        val arr = JSONArray()
        for (apt in aptitudes) arr.put(apt.toJSON())
        obj.put("aptitudes", arr)
        return obj
    }
}

data class AptitudeData(
    val id: String,
    val name: String,
    val color: ColorData,
    val categories: List<CategoryData>,
    val tags: List<String>,
    val order: Int,
    val modName: String,
    val originSkillId: String,
    val sections: List<SectionData>
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("name", name)
        obj.put("color", color.toJSON())
        val catArr = JSONArray()
        for (c in categories) catArr.put(c.toJSON())
        obj.put("categories", catArr)
        val tagArr = JSONArray()
        for (t in tags) tagArr.put(t)
        obj.put("tags", tagArr)
        obj.put("order", order)
        obj.put("modName", modName)
        obj.put("originSkillId", originSkillId)
        val secArr = JSONArray()
        for (s in sections) secArr.put(s.toJSON())
        obj.put("sections", secArr)
        return obj
    }
}

data class CategoryData(val id: String, val name: String, val color: ColorData) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("name", name)
        obj.put("color", color.toJSON())
        return obj
    }
}

data class SectionData(
    val canChooseMultiple: Boolean,
    val requiredPreviousSkills: Int,
    val skills: List<SkillData>
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("canChooseMultiple", canChooseMultiple)
        obj.put("requiredPreviousSkills", requiredPreviousSkills)
        val arr = JSONArray()
        for (s in skills) arr.put(s.toJSON())
        obj.put("skills", arr)
        return obj
    }
}

data class SkillData(
    val id: String,
    val name: String,
    val iconPath: String,
    val assetFileName: String,
    val affectsString: String,
    val order: Int,
    val modName: String,
    val isOriginSkill: Boolean,
    val tooltipElements: List<TooltipElement>
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("name", name)
        obj.put("iconPath", iconPath)
        obj.put("iconExportPath", "assets/$assetFileName")
        obj.put("assetFileName", assetFileName)
        obj.put("affectsString", affectsString)
        obj.put("order", order)
        obj.put("modName", modName)
        obj.put("isOriginSkill", isOriginSkill)
        val arr = JSONArray()
        for (e in tooltipElements) arr.put(e.toJSON())
        obj.put("tooltipElements", arr)
        return obj
    }
}

// Tooltip element hierarchy

interface TooltipElement {
    fun toJSON(): JSONObject
}

data class LabelElement(
    val text: String,
    val color: ColorData,
    val highlightRanges: List<HighlightRange>,
    val font: String?,
    val isTitle: Boolean = false,
    val padding: Float,
    val position: PositionData
) : TooltipElement {
    override fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("type", if (isTitle) "title" else "label")
        obj.put("text", text)
        obj.put("color", color.toJSON())
        val hlArr = JSONArray()
        for (hl in highlightRanges) hlArr.put(hl.toJSON())
        obj.put("highlightRanges", hlArr)
        if (font != null) obj.put("font", font)
        obj.put("padding", padding.toDouble())
        obj.put("position", position.toJSON())
        return obj
    }
}

data class HighlightRange(
    val startIndex: Int,
    val endIndex: Int,
    val text: String,
    val color: ColorData
) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("startIndex", startIndex)
        obj.put("endIndex", endIndex)
        obj.put("text", text)
        obj.put("color", color.toJSON())
        return obj
    }
}

data class SpacerElement(
    val height: Float,
    val position: PositionData
) : TooltipElement {
    override fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("type", "spacer")
        obj.put("height", height.toDouble())
        obj.put("position", position.toJSON())
        return obj
    }
}

data class ImageWithTextElement(
    val spriteName: String,
    val assetFileName: String,
    val imageHeight: Float,
    val padding: Float,
    val children: List<TooltipElement>,
    val position: PositionData
) : TooltipElement {
    override fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("type", "imageWithText")
        obj.put("spriteName", spriteName)
        obj.put("exportPath", if (assetFileName.isNotEmpty()) "assets/$assetFileName" else "")
        obj.put("assetFileName", assetFileName)
        obj.put("imageHeight", imageHeight.toDouble())
        obj.put("padding", padding.toDouble())
        val arr = JSONArray()
        for (c in children) arr.put(c.toJSON())
        obj.put("children", arr)
        obj.put("position", position.toJSON())
        return obj
    }
}

data class SectionHeadingElement(
    val text: String,
    val textColor: ColorData,
    val bgColor: ColorData,
    val alignment: String,
    val padding: Float,
    val position: PositionData
) : TooltipElement {
    override fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("type", "sectionHeading")
        obj.put("text", text)
        obj.put("textColor", textColor.toJSON())
        obj.put("bgColor", bgColor.toJSON())
        obj.put("alignment", alignment)
        obj.put("padding", padding.toDouble())
        obj.put("position", position.toJSON())
        return obj
    }
}

data class ImageElement(
    val spriteName: String,
    val assetFileName: String,
    val width: Float,
    val height: Float,
    val padding: Float,
    val position: PositionData
) : TooltipElement {
    override fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("type", "image")
        obj.put("spriteName", spriteName)
        obj.put("exportPath", if (assetFileName.isNotEmpty()) "assets/$assetFileName" else "")
        obj.put("assetFileName", assetFileName)
        obj.put("width", width.toDouble())
        obj.put("height", height.toDouble())
        obj.put("padding", padding.toDouble())
        obj.put("position", position.toJSON())
        return obj
    }
}

// Shared primitives

data class ColorData(val r: Int, val g: Int, val b: Int, val a: Int) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("r", r)
        obj.put("g", g)
        obj.put("b", b)
        obj.put("a", a)
        return obj
    }

    companion object {
        fun fromColor(color: Color): ColorData {
            return ColorData(color.red, color.green, color.blue, color.alpha)
        }
    }
}

data class PositionData(val x: Float, val y: Float, val width: Float, val height: Float) {
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("x", x.toDouble())
        obj.put("y", y.toDouble())
        obj.put("width", width.toDouble())
        obj.put("height", height.toDouble())
        return obj
    }
}
