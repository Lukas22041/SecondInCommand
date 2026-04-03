package second_in_command.ui.tutorial

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.specs.SCSpecStore

/**
 * Describes a single step in the skill-screen tutorial overlay.
 *
 * All coordinates are in UI space: origin at the *top-left* of the overlay panel,
 * Y increases downward. The [TutorialOverlayPlugin] converts them to GL screen-space
 * when rendering.
 *
 * @param focusX      Left edge of the highlighted rectangle (UI coords).
 * @param focusY      Top edge of the highlighted rectangle (UI coords).
 * @param focusWidth  Width of the highlighted rectangle.
 * @param focusHeight Height of the highlighted rectangle.
 * @param textBoxSide Where the text box should appear relative to the focus rect.
 * @param content     Extension lambda called with the content-area [TooltipMakerAPI]
 *                    to populate the text box. The border and navigation buttons are
 *                    added automatically by the overlay plugin.
 */
class TutorialStep(
    val focusX: Float,
    val focusY: Float,
    val focusWidth: Float,
    val focusHeight: Float,
    val textBoxSide: TextBoxSide = TextBoxSide.RIGHT,
    val textBoxWidth: Float = 360f,
    val textBoxHeight: Float = 150f,
    /** Explicit text-box top-left in UI space. When both are set they override [textBoxSide]. */
    val textBoxX: Float? = null,
    val textBoxY: Float? = null,
    /** When true the entire screen is darkened uniformly with no spotlight cutout.
     *  Use for steps (e.g. the welcome screen) that need no highlighted region. */
    val darkenFull: Boolean = false,
    /** Called when this step becomes the active step. Use to set up any demo game state. */
    val onEnter: (() -> Unit)? = null,
    /** Called when navigating away from this step (next, back, ESC). Use to restore demo state. */
    val onLeave: (() -> Unit)? = null,
    val content: TooltipMakerAPI.() -> Unit
) {
    enum class TextBoxSide { LEFT, RIGHT, TOP, BOTTOM, CENTER }

    companion object {

        /**
         * Returns the five default tutorial steps (Welcome + four content steps).
         *
         * @param isCompact   Whether the compact layout (SCSettings.enableCompactLayout) is active.
         * @param panelWidth  Width of the skill-menu panel.
         * @param panelHeight Height of the skill-menu panel.
         */
        fun buildDefaultSteps(
            isCompact: Boolean = false,
            panelWidth: Float = 800f,
            panelHeight: Float = 750f
        ): List<TutorialStep> {

            val officerSectionY  = if (isCompact) 295f else 305f
            val officerRowHeight = if (isCompact) 74f  else 96f

            // Mutable state captured by demo-step onEnter/onLeave lambdas.
            // Each call to buildDefaultSteps() creates fresh instances.
            var skillDemoSaved  = mutableMapOf<String, Int>()
            var xoDemoWasPlaced = false

            return listOf(

                // ── Step 0 – Welcome (focus = entire skill panel) ─────────────────────────
                TutorialStep(
                    focusX = 0f, focusY = 0f,
                    focusWidth = panelWidth, focusHeight = panelHeight,
                    textBoxSide = TextBoxSide.CENTER,
                    textBoxWidth = 400f, textBoxHeight = 160f,
                    darkenFull = true
                ) {
                    addSectionHeading("Welcome to Second-in-Command!", Alignment.MID, 0f)
                    addSpacer(8f)
                    addPara(
                        "This short tutorial will walk you through the new skill screen.",
                        0f
                    )
                    addSpacer(5f)
                    addPara(
                        "SiC replaces the vanilla aptitude trees with a new system: your commander " +
                        "gets personal combat skills, and Executive Officer slots provide " +
                        "fleet-wide bonuses.",
                        5f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "personal combat skills", "Executive Officer slots"
                    )
                    addSpacer(5f)
                    addPara(
                        "Press ESC at any time to exit the tutorial.",
                        0f, Misc.getGrayColor(), Misc.getHighlightColor(), "ESC"
                    )
                },

                // ── Step 1 – Player info column ───────────────────────────────────────────
                TutorialStep(
                    focusX = 10f, focusY = 22f, focusWidth = 305f, focusHeight = 265f,
                    textBoxSide = TextBoxSide.RIGHT,
                    textBoxX = 335f, textBoxY = 55f
                ) {
                    addSectionHeading("Your Commander", Alignment.MID, 0f)
                    addSpacer(6f)
                    addPara(
                        "This column shows your commander's portrait, current level, story " +
                        "points, and experience bar.",
                        0f
                    )
                    addSpacer(5f)
                    addPara(
                        "SiC removes the vanilla aptitude trees — instead your skill points are " +
                        "spent freely on the 14 combat skills shown to the right.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "skill points", "14 combat skills"
                    )
                },

                // ── Step 2 – Combat skill grid ────────────────────────────────────────────
                TutorialStep(
                    focusX = 310f, focusY = 22f,
                    focusWidth = panelWidth - 325f, focusHeight = 265f,
                    textBoxSide = TextBoxSide.LEFT,
                    textBoxX = 12f, textBoxY = 55f,
                    onEnter = {
                        val player  = Global.getSector().playerPerson
                        val allSkills = listOf(
                            "helmsmanship", "combat_endurance", "impact_mitigation",
                            "damage_control", "field_modulation", "target_analysis",
                            "systems_expertise", "point_defense", "energy_weapon_mastery",
                            "ballistic_mastery", "gunnery_implants", "ordnance_expert",
                            "polarized_armor", "missile_specialization"
                        )
                        // Save current skill levels so we can restore them on leave
                        skillDemoSaved = allSkills.associateWith { id ->
                            player.stats.skillsCopy.find { it.skill.id == id }?.level?.toInt() ?: 0
                        }.toMutableMap()
                        // Clear all demo skills then set the demonstration states:
                        //   helmsmanship     = active (level 1)
                        //   combat_endurance = active (level 1)
                        //   target_analysis  = elite  (level 2)
                        //   everything else  = inactive (level 0)
                        for (id in allSkills) player.stats.setSkillLevel(id, 0f)
                        player.stats.setSkillLevel("helmsmanship",     1f)
                        player.stats.setSkillLevel("combat_endurance", 1f)
                        player.stats.setSkillLevel("target_analysis",  2f)
                    },
                    onLeave = {
                        val player = Global.getSector().playerPerson
                        for ((id, level) in skillDemoSaved) {
                            player.stats.setSkillLevel(id, level.toFloat())
                        }
                        skillDemoSaved.clear()
                    }
                ) {
                    addSectionHeading("Combat Skill Grid", Alignment.MID, 0f)
                    addSpacer(6f)
                    addPara(
                        "These are your 14 combat skills. Click any icon to activate it — " +
                        "no prerequisites. You earn 1 skill point every 2 levels.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "1 skill point", "2 levels"
                    )
                    addSpacer(5f)
                    addPara(
                        "Hover any skill for its tooltip. Hover the Combat icon and press R " +
                        "to refund all skills for 1 story point.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "R", "1 story point"
                    )
                },

                // ── Step 3 – Executive Officer section ───────────────────────────────────
                TutorialStep(
                    focusX = 5f, focusY = officerSectionY - 5f,
                    focusWidth  = panelWidth - 25f,
                    focusHeight = panelHeight - officerSectionY - 15f,
                    textBoxSide = TextBoxSide.TOP,
                    textBoxHeight = 170f,
                    textBoxX = 30f, textBoxY = 35f
                ) {
                    addSectionHeading("Executive Officers", Alignment.MID, 0f)
                    addSpacer(6f)
                    addPara(
                        "These slots hold your Executive Officers (XOs). Each XO brings a unique " +
                        "aptitude and 5 skill points that provide fleet-wide bonuses.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "5 skill points", "fleet-wide bonuses"
                    )
                    addSpacer(5f)
                    addPara(
                        "They replace the vanilla Leadership, Technology, and Industry trees. " +
                        "Right-click an occupied slot to remove the officer.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "Leadership", "Technology", "Industry"
                    )
                },

                // ── Step 4 – XO slot demo ─────────────────────────────────────────────────
                TutorialStep(
                    focusX = 5f, focusY = officerSectionY + 18f,
                    focusWidth = panelWidth - 25f, focusHeight = officerRowHeight + 10f,
                    textBoxSide = TextBoxSide.RIGHT,
                    textBoxX = 30f, textBoxY = 35f,
                    onEnter = {
                        val data = SCUtils.getPlayerData()
                        if (data.getOfficerInSlot(0) == null) {
                            val aptSpec = SCSpecStore.getAptitudeSpecs().firstOrNull()
                            if (aptSpec != null) {
                                val officer = SCUtils.createRandomSCOfficer(aptSpec.id)
                                officer.skillPoints = 3
                                data.addOfficerToFleet(officer)
                                data.setOfficerInSlot(0, officer)
                                xoDemoWasPlaced = true
                            }
                        }
                    },
                    onLeave = {
                        if (xoDemoWasPlaced) {
                            val data    = SCUtils.getPlayerData()
                            val officer = data.getOfficerInSlot(0)
                            if (officer != null) {
                                data.setOfficerInSlot(0, null)
                                data.removeOfficerFromFleet(officer)
                            }
                            xoDemoWasPlaced = false
                        }
                    }
                ) {
                    addSectionHeading("Executive Officer — Example", Alignment.MID, 0f)
                    addSpacer(6f)
                    addPara(
                        "This is what a filled Executive Officer slot looks like. " +
                        "The officer's portrait, name, and aptitude colour are shown in the row.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "aptitude colour"
                    )
                    addSpacer(5f)
                    addPara(
                        "Click the slot to expand it and spend the officer's skill points. " +
                        "Each aptitude has its own unique set of fleet-wide skills to choose from.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "skill points", "fleet-wide skills"
                    )
                },

                // ── Step 5 – Officer picker slot ──────────────────────────────────────────
                TutorialStep(
                    focusX = 8f, focusY = officerSectionY + 28f,
                    focusWidth  = officerRowHeight + 4f,
                    focusHeight = officerRowHeight,
                    textBoxSide = TextBoxSide.RIGHT,
                    textBoxX = 130f, textBoxY = officerSectionY + 18f
                ) {
                    addSectionHeading("Hiring Officers", Alignment.MID, 0f)
                    addSpacer(6f)
                    addPara(
                        "Click an empty slot to open the officer picker.",
                        0f
                    )
                    addSpacer(5f)
                    addPara(
                        "Officers can be hired at the Comm Directory of friendly colonies, " +
                        "or found aboard derelict ships. Each officer brings a distinct aptitude " +
                        "colour and skill tree.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "Comm Directory", "derelict ships"
                    )
                }
            )
        }
    }
}


