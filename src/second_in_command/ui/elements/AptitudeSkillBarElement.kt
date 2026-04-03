package second_in_command.ui.elements

import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.SCData
import second_in_command.misc.SCThresholds
import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import second_in_command.ui.tooltips.SCSkillTooltipCreator

/**
 * Encapsulates the horizontal aptitude skill row:
 *   AptitudeBackgroundElement → origin SkillWidgetElement → SkillGapElement → sections (skills, separators, gaps, underlines)
 *
 * Use [backgroundPositioner] to place the background at its final position before the origin skill
 * is anchored to it. All subsequent elements chain off the background's position.
 *
 * @param officer          The officer whose active skills are reflected. Pass null for Codex / display-only mode:
 *                         all skills render as activated and non-interactive.
 * @param skillSize        Widget size in pixels. Use 64f for the Codex, 72f everywhere else.
 * @param openedFromPicker Passed through to [AptitudeBackgroundElement] to slightly shorten the bar strip.
 * @param allowSkillStateChange
 *                         When true (Skill Menu), each skill's canChangeState is set to !preAcquired so
 *                         unlocked skills can be toggled. When false (Picker / Bar / Codex) all skills are locked.
 * @param backgroundPositioner Called immediately after [backgroundElement] is created so it can be
 *                             placed at its final position **before** the origin skill is anchored
 *                             to it via `rightOfMid`. If null, the caller must position it after
 *                             construction (only safe when the bar is never dynamically recreated).
 */
class AptitudeSkillBarElement(
    val aptitudePlugin: SCBaseAptitudePlugin,
    val data: SCData,
    val officer: SCOfficer?,
    val parentElement: TooltipMakerAPI,
    val skillSize: Float = 72f,
    val openedFromPicker: Boolean = false,
    val allowSkillStateChange: Boolean = false,
    val backgroundPositioner: ((AptitudeBackgroundElement) -> Unit)? = null
) {

    val backgroundElement: AptitudeBackgroundElement
    val originSkillElement: SkillWidgetElement
    val skillElements = ArrayList<SkillWidgetElement>()
    val sections: MutableList<SCAptitudeSection>

    /** Called inside each skill widget's onClick handler. Wire this up after construction. */
    var onSkillClick: ((SkillWidgetElement) -> Unit)? = null

    init {
        val color = aptitudePlugin.getColor()

        backgroundElement = AptitudeBackgroundElement(color, parentElement, openedFromPicker)
        backgroundPositioner?.invoke(backgroundElement)

        sections = aptitudePlugin.getSections()

        // Origin skill – always activated, non-interactive
        val originSkill = SCSpecStore.getSkillSpec(aptitudePlugin.getOriginSkillId())!!
        originSkillElement = SkillWidgetElement(
            originSkill.id, aptitudePlugin.id,
            activated = true, canChangeState = false, preAcquired = true,
            iconPath = originSkill.iconPath, soundId = "leadership1",
            color = color, tooltip = parentElement,
            width = skillSize, height = skillSize
        )
        parentElement.addTooltipTo(
            SCSkillTooltipCreator(data, originSkill.getPlugin(), aptitudePlugin, 0, false),
            originSkillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW
        )
        originSkillElement.elementPanel.position.rightOfMid(backgroundElement.elementPanel, 20f)
        originSkillElement.onClick { originSkillElement.playClickSound() }

        val originGap = SkillGapElement(color, parentElement, heightOffset = skillSize)
        originGap.elementPanel.position.rightOfTop(originSkillElement.elementPanel, 0f)
        originGap.renderArrow = true

        val isCodexMode = officer == null
        val previousSections = ArrayList<SCAptitudeSection>()
        var previous: CustomPanelAPI = originGap.elementPanel

        for (section in sections) {
            val isLastSection = sections.last() == section
            val canOnlyChooseOne = !section.canChooseMultiple
            var firstSkillThisSection: SkillWidgetElement? = null
            var usedWidth = 0f

            section.previousUISections.addAll(previousSections)
            previousSections.add(section)

            val skills = section.getSkills()
            for (skill in skills) {
                val skillSpec = SCSpecStore.getSkillSpec(skill)!!
                val skillPlugin = skillSpec.getPlugin()!!

                val isFirst = skills.first() == skill
                val isLast = skills.last() == skill

                // In Codex mode: all skills displayed as fully active
                val preAcquired = if (isCodexMode) true else officer!!.activeSkillIDs.contains(skill)
                val activated = if (isCodexMode) true else officer!!.activeSkillIDs.contains(skill)
                val canChangeState = if (isCodexMode || !allowSkillStateChange) false else !preAcquired

                val skillElement = SkillWidgetElement(
                    skill, aptitudePlugin.id,
                    activated = activated, canChangeState = canChangeState, preAcquired = preAcquired,
                    iconPath = skillPlugin.getIconPath(), soundId = section.soundId,
                    color = color, tooltip = parentElement,
                    width = skillSize, height = skillSize
                )
                skillElements.add(skillElement)
                section.activeSkillsInUI.add(skillElement)
                usedWidth += skillSize

                val tooltip = SCSkillTooltipCreator(
                    data, skillPlugin, aptitudePlugin,
                    section.requiredPreviousSkills, !section.canChooseMultiple
                )
                parentElement.addTooltipTo(tooltip, skillElement.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW)
                section.tooltips.add(tooltip)

                if (firstSkillThisSection == null) firstSkillThisSection = skillElement

                if (isFirst) {
                    skillElement.elementPanel.position.rightOfTop(previous, 0f)
                } else {
                    skillElement.elementPanel.position.rightOfTop(previous, 3f)
                    usedWidth += 3f
                }

                if (!isLast) {
                    val separator = SkillSeperatorElement(color, parentElement, heightOverride = skillSize)
                    separator.elementPanel.position.rightOfTop(skillElement.elementPanel, 3f)
                    previous = separator.elementPanel
                    usedWidth += 3f
                } else if (!isLastSection) {
                    val gap = SkillGapElement(color, parentElement, heightOffset = skillSize)
                    // In Codex mode arrows are always visible; in officer modes they are updated dynamically
                    gap.renderArrow = isCodexMode
                    gap.elementPanel.position.rightOfTop(skillElement.elementPanel, 0f)
                    previous = gap.elementPanel

                    val nextSection = sections.getOrNull(sections.indexOf(section) + 1)
                    if (nextSection != null) nextSection.uiGap = gap
                }

                if (canOnlyChooseOne) {
                    val underline = SkillUnderlineElement(color, 2f, parentElement, usedWidth)
                    underline.position.belowLeft(firstSkillThisSection!!.elementPanel, 2f)
                }

                skillElement.onClick {
                    onSkillClick?.invoke(skillElement)
                }
            }
        }

        // Mark distribution-activated skills (skills activated by Distribution Tactics at 50% threshold)
        // Only set the visual flag — do NOT set activated/preAcquired so they don't consume skill points.
        if (!isCodexMode && officer != null && aptitudePlugin.id == "sc_tactical") {
            val distributedIds = data.getDistributionActivatedSkillIds()
            for (skillElement in skillElements) {
                if (distributedIds.contains(skillElement.id) && !skillElement.preAcquired) {
                    skillElement.isDistributionActivated = true
                }
            }
            // Also mark the corresponding tooltips
            for (section in sections) {
                for (tooltip in section.tooltips) {
                    if (distributedIds.contains(tooltip.skill.getId())) {
                        tooltip.isDistributionActivated = true
                    }
                }
            }
        }

        // In Codex mode, mark sections that have prerequisites as "not yet met" for tooltips
        if (isCodexMode) {
            for (section in sections) {
                if (section.requiredPreviousSkills >= 1) {
                    section.tooltips.forEach { it.sectionMeetsRequirements = false }
                }
            }
        }
    }

    /**
     * Performs a one-shot static recalculation of section requirements based on the officer's
     * currently active skills. Call this after construction for Picker and Bar contexts.
     * For the Skill Menu, use the more complete recalculateSectionRequirements() defined there instead.
     */
    fun recalculateInitialSectionRequirements() {
        for (section in sections) {
            val count = section.previousUISections.sumOf { prev ->
                prev.activeSkillsInUI.count { it.activated }
            }
            val meetsRequirements = section.requiredPreviousSkills <= count
            section.uiGap?.renderArrow = meetsRequirements
            section.tooltips.forEach { it.sectionMeetsRequirements = meetsRequirements }
        }
    }

    /**
     * Returns how many skill points the officer has remaining after accounting for
     * skills that have been toggled on in the UI but not yet saved to the officer.
     */
    fun calculateRemainingSP(): Int {
        if (officer == null) return 0
        val newSkills = skillElements.filter { !officer.activeSkillIDs.contains(it.id) && it.activated }
        return officer.skillPoints - newSkills.count()
    }
}




