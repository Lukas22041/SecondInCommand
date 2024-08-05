package second_in_command.specs

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import second_in_command.SCData
import java.awt.Color

/**Base Plugin for Aptitudes. */
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

    abstract fun getOriginSkillId() : String

    fun getOriginSkillSpec() = SCSpecStore.getSkillSpec(getOriginSkillId())

    fun getOriginSkillPlugin() = getOriginSkillSpec()!!.getPlugin()

    abstract fun createSections()

    abstract fun getNPCSpawnWeight(data: SCData, fleet: CampaignFleetAPI) : Float



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

    open fun getSpawnWeight() : Float {
        return spec.spawnWeight
    }

    fun getTags() : List<String> {
        return spec.tags
    }

}