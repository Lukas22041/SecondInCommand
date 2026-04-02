package second_in_command.ui.elements

import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaUI.elements.LunaElement
import second_in_command.SCData
import second_in_command.specs.SCOfficer
import com.fs.starfarer.api.ui.TooltipMakerAPI
import second_in_command.misc.addTooltip
import java.awt.Color

/**
 * Builds the full officer-card row into [parentElement]:
 *   spacer → SCOfficerPickerElement → (optional) name label → SkillUnderlineElement
 *   → offset anchor → AptitudeSkillBarElement (background + skills)
 *   → (optional) category display → SP para anchor
 *
 * After construction:
 *  - Attach click/hover/input handlers to [officerPickerElement]
 *  - Set [skillBar].onSkillClick for skill interaction
 *  - Add tooltips to [officerPickerElement].elementPanel from the parent scrollerElement
 *  - Use [spParaAnchor] to position your SP counter para:
 *      parentElement.addPara(...).position.rightOfBottom(spParaAnchor.elementPanel, 0f)
 *
 * @param officerSize      Portrait widget size. Pass 86f when compact layout is active.
 * @param openedFromPicker false when used in SCSkillMenuPanel (adjusts name-label anchoring).
 * @param showCategory     Show the category row below the portrait (used in Picker and Bar).
 * @param showNameLabel    Show the aptitude name label above the portrait (hidden in compact layout).
 * @param allowSkillStateChange
 *                         Forwarded to [AptitudeSkillBarElement]; set true in SCSkillMenuPanel.
 */
class OfficerAptitudeRowElement(
    val officer: SCOfficer,
    val data: SCData,
    val parentElement: TooltipMakerAPI,
    val officerSize: Float = 96f,
    val openedFromPicker: Boolean = true,
    val showCategory: Boolean = true,
    val showNameLabel: Boolean = true,
    val allowSkillStateChange: Boolean = false,
    /**
     * When true (Picker / Bar), a 24px spacer is prepended so the aptitude name label
     * can sit inside the LunaElement card above the portrait.
     * When false (SkillMenu), no spacer is added; the label is instead anchored
     * aboveLeft the portrait and floats into the gap between rows.
     */
    val addLeadingSpacer: Boolean = true
) {

    val officerPickerElement: SCOfficerPickerElement
    val skillBar: AptitudeSkillBarElement

    /** Zero-size anchor positioned aboveLeft the origin skill. Use this to position your SP para. */
    val spParaAnchor: LunaElement

    /** The category background element when category display is visible, or null otherwise. */
    var categoryBackground: LunaElement? = null

    init {
        val aptitudePlugin = officer.getAptitudePlugin()
        val color = aptitudePlugin.getColor()

        // Top spacer so the portrait sits below the name label area (only when content is
        // inside a LunaElement card — not needed when the label floats above the subpanel)
        if (addLeadingSpacer) parentElement.addSpacer(24f)

        // Officer portrait
        officerPickerElement = SCOfficerPickerElement(officer.person, color, parentElement, officerSize, officerSize)

        // Aptitude name label above the portrait
        if (showNameLabel) {
            val paraElement = parentElement.addLunaElement(100f, 20f).apply {
                renderBorder = false
                renderBackground = false
            }
            if (openedFromPicker) {
                paraElement.elementPanel.position.aboveMid(officerPickerElement.elementPanel, 0f)
            } else {
                paraElement.position.aboveLeft(officerPickerElement.elementPanel, 0f)
            }
            val textOffsetX = if (openedFromPicker) -1f else -3f
            paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")
            val aptitudePara = paraElement.innerElement.addPara(
                aptitudePlugin.getName(), 0f, color, color
            )
            aptitudePara.position.inTL(
                paraElement.width / 2 - aptitudePara.computeTextWidth(aptitudePara.text) / 2 + textOffsetX,
                paraElement.height - aptitudePara.computeTextHeight(aptitudePara.text) - 5
            )
        }

        // Underline beneath the portrait
        val officerUnderline = SkillUnderlineElement(color, 2f, parentElement, officerSize)
        officerUnderline.position.belowLeft(officerPickerElement.elementPanel, 2f)

        // Zero-size offset anchor to position the skill bar background
        val offsetElement = parentElement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfMid(officerPickerElement.elementPanel, -1f)

        // Skill bar (creates background element internally)
        skillBar = AptitudeSkillBarElement(
            aptitudePlugin = aptitudePlugin,
            data = data,
            officer = officer,
            parentElement = parentElement,
            skillSize = 72f,
            openedFromPicker = openedFromPicker,
            allowSkillStateChange = allowSkillStateChange,
            backgroundPositioner = { bg ->
                bg.elementPanel.position.belowLeft(offsetElement.elementPanel, 10f)
            }
        )

        // Optional category display
        val categories = aptitudePlugin.categories
        if (showCategory && categories.isNotEmpty()) {
            val anchor = parentElement.addLunaElement(20f, 20f).apply {
                renderBackground = false
                renderBorder = false
            }
            anchor.elementPanel.position.belowLeft(officerPickerElement.elementPanel, 8f)

            val categoryNames = ArrayList<String>()
            val categoryColors = ArrayList<Color>()
            var categoryText = ""

            for (category in categories) {
                categoryNames.add(category.name)
                categoryColors.add(Misc.getTextColor())
                categoryText += "${category.name}, "
            }
            categoryText = categoryText.trim().trimEnd(',')

            val extraS = if (categories.size >= 2) "ies" else "y"

            val label = parentElement.addPara(
                "Categor$extraS: $categoryText", 0f,
                Misc.getGrayColor(), Misc.getHighlightColor()
            )
            label.position.rightOfMid(anchor.elementPanel, -16f)
            label.setHighlight("Categor$extraS:", *categoryNames.toTypedArray())
            label.setHighlightColors(color, *categoryColors.toTypedArray())

            val length = label.computeTextWidth(label.text)

            categoryBackground = parentElement.addLunaElement(length + 8 + 4, 24f).apply {
                enableTransparency = true
                renderBackground = false
                renderBorder = false
            }
            categoryBackground!!.elementPanel.position.rightOfMid(anchor.elementPanel, -16f - 4)

            parentElement.addTooltip(
                categoryBackground!!.elementPanel, TooltipMakerAPI.TooltipLocation.BELOW, 250f
            ) { tooltip ->
                tooltip.addPara(
                    "Some aptitudes are part of a category. You can not assign multiple officers of the same category at the same time.",
                    0f, Misc.getTextColor(), Misc.getHighlightColor(),
                    "category", "can not assign two officers of the same category at the same time"
                )
            }
        }

        // SP para anchor – positioned above the origin skill so callers can place the para label
        spParaAnchor = parentElement.addLunaElement(0f, 0f)
        spParaAnchor.elementPanel.position.aboveLeft(skillBar.originSkillElement.elementPanel, 6f)
    }
}




