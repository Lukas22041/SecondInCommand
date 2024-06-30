package second_in_command.specs

abstract class SCBaseAptitudePlugin() {

    lateinit var spec: SCAptitudeSpec

    private var sections = ArrayList<SCAptitudeSection>()

    fun addSection(section: SCAptitudeSection) {
        sections.add(section)
    }

    abstract fun getSpawnWeight() : Float

    abstract fun getOriginSkillId() : String

    abstract fun createSections()

}