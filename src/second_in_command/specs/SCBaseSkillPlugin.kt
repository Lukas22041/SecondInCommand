package second_in_command.specs

import com.fs.starfarer.api.ui.TooltipMakerAPI

abstract class SCBaseSkillPlugin {

    lateinit var spec: SCSkillSpec

    fun getId() : String{
        return spec.id
    }

    fun getIconPath() : String{
        return spec.iconPath
    }

    open fun getName() : String {
        return spec.name
    }

    abstract fun getAffectsString() : String

    abstract fun addTooltip(tooltip: TooltipMakerAPI)

}