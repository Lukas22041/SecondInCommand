package second_in_command.buildscript

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Factions
import org.apache.log4j.Level
import second_in_command.SCData
import second_in_command.specs.SCSpecStore
import second_in_command.ui.tooltips.SCSkillTooltipCreator
import java.text.SimpleDateFormat
import java.util.*

/**
 * Build script that generates a JSON API and exports assets for all SC aptitudes and skills.
 * Only runs when SC_DEV_BUILD environment variable is set.
 * Registered as a transient EveryFrameScript — runs once on the first frame, then removes itself.
 */
class SCBuildScript : EveryFrameScript {

    private val logger = Global.getLogger(SCBuildScript::class.java).apply { level = Level.ALL }
    private var done = false
    private var ranOnce = false

    override fun isDone(): Boolean = done
    override fun runWhilePaused(): Boolean = true

    override fun advance(amount: Float) {
        if (ranOnce) {
            done = true
            return
        }
        ranOnce = true

        try {
            logger.info("SC Build: Starting export...")
            val startTime = System.currentTimeMillis()

            val exportData = buildExportData()
            val spritePaths = collectSpritePaths(exportData)
            SCBuildExporter.export(exportData, spritePaths)

            val elapsed = System.currentTimeMillis() - startTime
            logger.info("SC Build: Finished in ${elapsed}ms")
        } catch (e: Exception) {
            logger.error("SC Build: Export failed", e)
        }

        done = true
    }

    private fun buildExportData(): BuildExportData {
        // Create a temporary fleet + SCData (same pattern as CodexHandler)
        val fleet = Global.getFactory().createEmptyFleet(
            Global.getSettings().createBaseFaction(Factions.NEUTRAL), false
        )
        val data = SCData(fleet)

        val aptitudes = mutableListOf<AptitudeData>()

        for (aptSpec in SCSpecStore.getAptitudeSpecs().sortedBy { it.order }) {
            try {
                val aptPlugin = aptSpec.getPlugin()
                val sections = aptPlugin.getSections()
                val originSkillId = aptPlugin.getOriginSkillId()

                val sectionDataList = mutableListOf<SectionData>()

                // Add origin skill as its own implicit section
                val originSkillSpec = SCSpecStore.getSkillSpec(originSkillId)
                if (originSkillSpec != null) {
                    val originSkillData = buildSkillData(
                        data, originSkillSpec.getPlugin(), aptPlugin,
                        requiredSkillPoints = 0, pickOnlyOne = false, isOrigin = true
                    )
                    sectionDataList.add(SectionData(
                        canChooseMultiple = true,
                        requiredPreviousSkills = 0,
                        skills = listOf(originSkillData)
                    ))
                }

                // Process each section
                for (section in sections) {
                    val skillDataList = mutableListOf<SkillData>()
                    for (skillId in section.getSkills()) {
                        val skillSpec = SCSpecStore.getSkillSpec(skillId) ?: continue
                        val skillData = buildSkillData(
                            data, skillSpec.getPlugin(), aptPlugin,
                            section.requiredPreviousSkills, !section.canChooseMultiple,
                            isOrigin = false
                        )
                        skillDataList.add(skillData)
                    }
                    sectionDataList.add(SectionData(
                        canChooseMultiple = section.canChooseMultiple,
                        requiredPreviousSkills = section.requiredPreviousSkills,
                        skills = skillDataList
                    ))
                }

                aptitudes.add(AptitudeData(
                    id = aptSpec.id,
                    name = aptSpec.name,
                    color = ColorData.fromColor(aptSpec.color),
                    categories = aptSpec.categories.map {
                        CategoryData(it.id, it.name, ColorData.fromColor(it.color))
                    },
                    tags = aptSpec.tags,
                    order = aptSpec.order,
                    modName = aptSpec.modSpec.name,
                    originSkillId = originSkillId,
                    sections = sectionDataList
                ))

                logger.info("SC Build: Processed aptitude '${aptSpec.name}' with ${sectionDataList.sumOf { it.skills.size }} skills")
            } catch (e: Exception) {
                logger.error("SC Build: Failed to process aptitude '${aptSpec.id}'", e)
            }
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return BuildExportData(
            version = Global.getSettings().modManager.getModSpec("second_in_command").version,
            exportDate = dateFormat.format(Date()),
            tooltipWidth = 700f,
            aptitudes = aptitudes
        )
    }

    /**
     * Build skill data by rendering its tooltip through the recording proxy.
     */
    private fun buildSkillData(
        data: SCData,
        skillPlugin: second_in_command.specs.SCBaseSkillPlugin,
        aptPlugin: second_in_command.specs.SCBaseAptitudePlugin,
        requiredSkillPoints: Int,
        pickOnlyOne: Boolean,
        isOrigin: Boolean
    ): SkillData {
        // Create a real panel and tooltip for rendering
        val panel = Global.getSettings().createCustom(700f, 1000f, null)
        val realTooltip = panel.createUIElement(700f, 1000f, false)
        panel.addUIElement(realTooltip)

        // Wrap in recording delegator
        val recorder = RecordingTooltipMaker(realTooltip)

        // Use SCSkillTooltipCreator to render the full tooltip (title, affects, skill content, warnings)
        val creator = SCSkillTooltipCreator(data, skillPlugin, aptPlugin, requiredSkillPoints, pickOnlyOne)
        creator.createTooltip(recorder, false, null)

        // Parse recorded calls + rendered children into elements
        val elements = TooltipElementParser.parse(recorder.recordedCalls, realTooltip)

        return SkillData(
            id = skillPlugin.id,
            name = skillPlugin.name,
            iconPath = skillPlugin.iconPath,
            assetFileName = TooltipElementParser.spriteToAssetName(skillPlugin.iconPath),
            affectsString = skillPlugin.affectsString,
            order = skillPlugin.spec.order,
            modName = skillPlugin.spec.modname,
            isOriginSkill = isOrigin,
            tooltipElements = elements
        )
    }

    /**
     * Collect all sprite paths referenced in the export for asset copying.
     */
    private fun collectSpritePaths(data: BuildExportData): Set<String> {
        val paths = mutableSetOf<String>()

        for (apt in data.aptitudes) {
            for (section in apt.sections) {
                for (skill in section.skills) {
                    // Skill icon
                    paths.add(skill.iconPath)

                    // Sprites from tooltip elements
                    collectSpritePathsFromElements(skill.tooltipElements, paths)
                }
            }
        }

        return paths
    }

    private fun collectSpritePathsFromElements(elements: List<TooltipElement>, paths: MutableSet<String>) {
        for (element in elements) {
            when (element) {
                is ImageWithTextElement -> {
                    if (element.spriteName.isNotEmpty()) paths.add(element.spriteName)
                    collectSpritePathsFromElements(element.children, paths)
                }
                is ImageElement -> {
                    if (element.spriteName.isNotEmpty()) paths.add(element.spriteName)
                }
            }
        }
    }
}
