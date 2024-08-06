package second_in_command.specs

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
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

    abstract fun getNPCFleetSpawnWeight(data: SCData, fleet: CampaignFleetAPI) : Float

    open fun getMarketSpawnweight(market: MarketAPI) : Float {
        return spec.spawnWeight
    }

    open fun getCryopodSpawnWeight(system: StarSystemAPI) : Float {
        return spec.spawnWeight
    }

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

    fun getTags() : List<String> {
        return spec.tags
    }

}