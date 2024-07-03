package second_in_command.specs

import java.awt.Color

abstract class SCBaseAptitudePlugin() {

    lateinit var spec: SCAptitudeSpec

    private var sections = ArrayList<SCAptitudeSection>()

    fun addSection(section: SCAptitudeSection) {
        sections.add(section)
    }

    fun getSections() = ArrayList(sections)

    fun clearSections() {
        sections.clear()
    }

    abstract fun getSpawnWeight() : Float

    abstract fun getOriginSkillId() : String

    abstract fun createSections()




    fun getId() : String{
        return spec.id
    }

    fun getCategory() : String{
        return spec.category
    }

    open fun getName() : String {
        return spec.name
    }

    open fun getColor() : Color{
        return spec.color
    }

    open fun getMaxLevel() : Int {
        return spec.maxLevel
    }

    open fun getXPGainMult() : Float {
        return spec.xpGainMult
    }

    open fun getRequiresDock() : Boolean {
        return spec.requiresDock
    }

}