package second_in_command.specs

import java.awt.Color

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

}