package second_in_command.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI

fun UIPanelAPI.getChildrenCopy() : List<UIComponentAPI> {
    return ReflectionUtils.invoke("getChildrenCopy", this) as List<UIComponentAPI>
}

fun UIPanelAPI.getChildrenNonCopy() : List<UIComponentAPI>  {
    return ReflectionUtils.invoke("getChildrenNonCopy", this) as List<UIComponentAPI>
}

fun UIPanelAPI.getWidth() : Float  {
    return ReflectionUtils.invoke("getWidth", this) as Float
}

fun UIPanelAPI.getHeight() : Float  {
    return ReflectionUtils.invoke("getHeight", this) as Float
}

fun UIComponentAPI.getWidth() : Float  {
    return ReflectionUtils.invoke("getWidth", this) as Float
}

fun UIComponentAPI.getHeight() : Float  {
    return ReflectionUtils.invoke("getHeight", this) as Float
}

fun UIPanelAPI.clearChildren() {
    ReflectionUtils.invoke("clearChildren", this)
}

fun UIComponentAPI.getParent() : UIPanelAPI?  {
    return ReflectionUtils.invoke("getParent", this) as UIPanelAPI
}

fun TooltipMakerAPI.getParentWidget() : UIComponentAPI? {
    return ReflectionUtils.invoke("getParentWidget", this) as UIPanelAPI
}

fun UIComponentAPI.setOpacity(alpha: Float)
{
    ReflectionUtils.invoke("setOpacity", this, alpha)
}

fun UIComponentAPI.getOpacity() : Float
{
    return ReflectionUtils.invoke("getOpacity", this) as Float
}

