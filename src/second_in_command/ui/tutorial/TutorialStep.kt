package second_in_command.ui.tutorial

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCUtils
import second_in_command.specs.SCOfficer

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
         * Returns the nine default tutorial steps.
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
            // Height covering portrait icons for all three slots (rows + their spacers).
            val threeSlotHeight  = 3f * (officerRowHeight + 30f) - 30f

            // Per-step mutable demo state captured by onEnter/onLeave lambdas.
            var combatDemoSaved     = mutableMapOf<String, Int>()
            var capstoneDemoSaved   = mutableMapOf<String, Int>()
            var skillLevelDemoSaved = mutableMapOf<String, Int>()
            var savedSlot0Officer: SCOfficer? = null
            var demoEngineerOfficer: SCOfficer? = null

            val allCombatSkills = listOf(
                "helmsmanship", "combat_endurance", "impact_mitigation",
                "damage_control", "field_modulation", "target_analysis",
                "systems_expertise", "point_defense", "energy_weapon_mastery",
                "ballistic_mastery", "gunnery_implants", "ordnance_expert",
                "polarized_armor", "missile_specialization"
            )

            return listOf(

                // ── Step 0 – Welcome ──────────────────────────────────────────────────────
                TutorialStep(
                    focusX = 0f, focusY = 0f,
                    focusWidth = panelWidth, focusHeight = panelHeight,
                    textBoxSide = TextBoxSide.CENTER,
                    textBoxWidth = 460f,
                    darkenFull = true
                ) {
                    addSectionHeading("Welcome to Second-in-Command!", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "This is a short introduction to Second-in-Command. It will showcase " +
                        "differences to the vanilla skill system and explain some fundamentals.",
                        0f
                    )
                    addSpacer(10f)
                    addPara(
                        "Press ESC at any time to close this tutorial.",
                        0f, Misc.getGrayColor(), Misc.getHighlightColor(), "ESC"
                    )
                },

                // ── Step 1 – The Player ───────────────────────────────────────────────────
                TutorialStep(
                    focusX = 10f, focusY = 22f, focusWidth = 305f, focusHeight = 265f,
                    textBoxSide = TextBoxSide.RIGHT,
                    textBoxWidth = 360f,
                    textBoxX = 330f, textBoxY = 40f
                ) {
                    addSectionHeading("The Player", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "This section displays your current level, skill points, and story points.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "skill points", "story points"
                    ).also { it.setHighlightColors(Misc.getHighlightColor(), Misc.getStoryOptionColor()) }
                    addSpacer(10f)
                    addPara(
                        "In Second-in-Command, skill points are only acquired on every second " +
                        "level up, for a total of 8 at the maximum level.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "every second level up", "8"
                    )
                    addSpacer(10f)
                    addPara(
                        "You can hover over the experience bar to see additional effects gained " +
                        "at certain level milestones.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "experience bar"
                    )
                },

                // ── Step 2 – Combat Skills ────────────────────────────────────────────────
                TutorialStep(
                    focusX = 310f, focusY = 22f,
                    focusWidth = panelWidth - 325f, focusHeight = 265f,
                    textBoxSide = TextBoxSide.LEFT,
                    textBoxWidth = 290f,
                    textBoxX = 12f, textBoxY = 35f,
                    onEnter = {
                        val player = Global.getSector().playerPerson
                        combatDemoSaved = allCombatSkills.associateWith { id ->
                            player.stats.skillsCopy.find { it.skill.id == id }?.level?.toInt() ?: 0
                        }.toMutableMap()
                        for (id in allCombatSkills) player.stats.setSkillLevel(id, 0f)
                        player.stats.setSkillLevel("helmsmanship",     1f)
                        player.stats.setSkillLevel("combat_endurance", 1f)
                        player.stats.setSkillLevel("target_analysis",  2f)
                    },
                    onLeave = {
                        val player = Global.getSector().playerPerson
                        for ((id, level) in combatDemoSaved) player.stats.setSkillLevel(id, level.toFloat())
                        combatDemoSaved.clear()
                    }
                ) {
                    addSectionHeading("Combat Skills", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "Your skill points are used exclusively on combat skills. All 14 combat skills " +
                        "from the base game have been moved here. Unlike the base game, there is " +
                        "no required order to unlock them.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "14","no required order"
                    )
                    addSpacer(10f)
                    addPara(
                        "You can re-specialize your skill loadout by pressing R while hovering " +
                        "over the Combat icon, at the cost of one story point.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "R", "Combat", "story point"
                    ).also { it.setHighlightColors(Misc.getHighlightColor(), Misc.getHighlightColor(), Misc.getStoryOptionColor()) }
                },

                // ── Step 3 – Combat Capstones ─────────────────────────────────────────────
                TutorialStep(
                    focusX = 975f, focusY = 22f, focusWidth = 100f, focusHeight = 265f,
                    textBoxSide = TextBoxSide.LEFT,
                    textBoxWidth = 370f,
                    textBoxX = 975-20f-370f, textBoxY = 50f,
                    onEnter = {
                        val player = Global.getSector().playerPerson
                        capstoneDemoSaved = allCombatSkills.associateWith { id ->
                            player.stats.skillsCopy.find { it.skill.id == id }?.level?.toInt() ?: 0
                        }.toMutableMap()
                        for (id in allCombatSkills) player.stats.setSkillLevel(id, 0f)
                        player.stats.setSkillLevel("systems_expertise",      1f)
                        player.stats.setSkillLevel("missile_specialization", 1f)
                    },
                    onLeave = {
                        val player = Global.getSector().playerPerson
                        for ((id, level) in capstoneDemoSaved) player.stats.setSkillLevel(id, level.toFloat())
                        capstoneDemoSaved.clear()
                    }
                ) {
                    addSectionHeading("Combat Capstones", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "All combat skills cost 1 skill point to acquire, with the exception of " +
                        "Missile Specialization and Systems Expertise.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "Missile Specialization", "Systems Expertise"
                    )
                    addSpacer(10f)
                    addPara(
                        "As the former capstones of the combat tree, these two skills each cost " +
                        "2 skill points to acquire.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "2 skill points"
                    )
                },

                // ── Step 4 – Skill Levels ─────────────────────────────────────────────────
                TutorialStep(
                    focusX = 420f, focusY = 22f, focusWidth = 330f, focusHeight = 180f,
                    textBoxSide = TextBoxSide.BOTTOM,
                    textBoxWidth = 400f,
                    textBoxX = 200f, textBoxY = 160f,
                    onEnter = {
                        val player = Global.getSector().playerPerson
                        skillLevelDemoSaved = allCombatSkills.associateWith { id ->
                            player.stats.skillsCopy.find { it.skill.id == id }?.level?.toInt() ?: 0
                        }.toMutableMap()
                        for (id in allCombatSkills) player.stats.setSkillLevel(id, 0f)
                        // helmsmanship = inactive (level 0)
                        // combat_endurance = active (level 1)
                        // impact_mitigation = elite (level 2)
                        player.stats.setSkillLevel("combat_endurance", 1f)
                        player.stats.setSkillLevel("impact_mitigation", 2f)
                    },
                    onLeave = {
                        val player = Global.getSector().playerPerson
                        for ((id, level) in skillLevelDemoSaved) player.stats.setSkillLevel(id, level.toFloat())
                        skillLevelDemoSaved.clear()
                    }
                ) {
                    addSectionHeading("Skill Levels", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "Like in the base game, combat skills can be made elite by spending a " +
                        "story point on them.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "elite", "story point"
                    ).also { it.setHighlightColors(Misc.getHighlightColor(), Misc.getStoryOptionColor()) }
                    addSpacer(10f)
                    addPara(
                        "Unlike the base game, you can right-click a skill to reclaim the story " +
                        "point and un-elite it at no additional cost.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "right-click", "story point"
                    ).also { it.setHighlightColors(Misc.getHighlightColor(), Misc.getStoryOptionColor()) }
                },

                // ── Step 5 – Executive Officers ───────────────────────────────────────────
                TutorialStep(
                    focusX = 5f, focusY = officerSectionY - 5f,
                    focusWidth  = panelWidth - 10f,
                    focusHeight = panelHeight - officerSectionY ,
                    textBoxSide = TextBoxSide.TOP,
                    textBoxWidth = 430f,
                    textBoxX = 30f, textBoxY = 35f
                ) {
                    addSectionHeading("Executive Officers", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "Executive Officers (XOs) are the main feature of the mod. They replace " +
                        "the Leadership, Technology, and Industry aptitudes from vanilla.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "Leadership", "Technology", "Industry"
                    )
                    addSpacer(10f)
                    addPara(
                        "You can assign Executive Officers, each one coming with their own Aptitude " +
                        "and set of fleet-wide skills. Second-in-Command has 12 different aptitude types.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "12 different"
                    )
                    addSpacer(10f)
                    addPara(
                        "Executive Officers have nothing to do with normal ship officers, those " +
                        "remain completely unchanged.",
                        0f
                    )
                },

                // ── Step 6 – Selecting Skills ─────────────────────────────────────────────
                TutorialStep(
                    focusX = 5f + 5, focusY = officerSectionY + 18f + 5,
                    focusWidth  = panelWidth - 20f,
                    focusHeight = officerRowHeight + 40f,
                    textBoxSide = TextBoxSide.TOP,
                    textBoxWidth = 430f,
                    textBoxX = 30f, textBoxY = 35f,
                    onEnter = {
                        val data = SCUtils.getPlayerData()
                        savedSlot0Officer = data.getOfficerInSlot(0)
                        val officer = SCUtils.createRandomSCOfficer("sc_engineering")
                        data.addOfficerToFleet(officer)
                        data.setOfficerInSlot(0, officer)
                        demoEngineerOfficer = officer
                    },
                    onLeave = {
                        val demo = demoEngineerOfficer
                        if (demo != null) {
                            val data = SCUtils.getPlayerData()
                            data.setOfficerInSlot(0, savedSlot0Officer)
                            data.removeOfficerFromFleet(demo)
                            demoEngineerOfficer = null
                            savedSlot0Officer = null
                        }
                    }
                ) {
                    addSectionHeading("Selecting Skills", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "Every Executive Officer levels up separately and independently from another. With each " +
                        "level-up they gain a skill point, up to a maximum of 5.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "5"
                    )
                    addSpacer(10f)
                    addPara(
                        "All XOs have a skill that is always active. That skill is their \"Origin Skill\".",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "Origin Skill"
                    )
                    addSpacer(10f)
                    addPara(
                        "Some aptitudes belong to a Category and cannot be used alongside other " +
                        "aptitudes of the same category. The Engineering aptitude, that is shown here as an example, is part of " +
                        "the Logistical category.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "Engineering", "Logistical"
                    )
                },

                // ── Step 7 – Acquiring Officers ───────────────────────────────────────────
                TutorialStep(
                    focusX = 8f + 10, focusY = officerSectionY + 28f,
                    focusWidth  = officerRowHeight + 4f + 20f,
                    focusHeight = threeSlotHeight + 40f,
                    textBoxSide = TextBoxSide.RIGHT,
                    textBoxWidth = 390f,
                    textBoxX = 170f, textBoxY = officerSectionY + 18f + 10f
                ) {
                    addSectionHeading("Acquiring Officers", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "You can assign a new officer by clicking on an empty officer slot.",
                        0f
                    )
                    addSpacer(10f)
                    addPara(
                        "By default, up to 3 Executive Officers can be active at once. All " +
                        "others are held in reserve. Officers in reserve still gain 50%% of the " +
                        "experience that active officers gain.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(), "3", "50%"
                    )
                    addSpacer(10f)
                    addPara(
                        "At the start you won't have any XOs yet. They are most commonly found " +
                        "at the comm directory of colonies or discovered aboard derelict ships.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "comm directory", "derelict ships"
                    )
                },

                // ── Step 8 – Get Started ──────────────────────────────────────────────────
                TutorialStep(
                    focusX = 0f, focusY = 0f,
                    focusWidth = panelWidth, focusHeight = panelHeight,
                    textBoxSide = TextBoxSide.CENTER,
                    textBoxWidth = 420f,
                    darkenFull = true
                ) {
                    addSectionHeading("Get Started", Alignment.MID, 0f)
                    addSpacer(10f)
                    addPara(
                        "With this, you know the basics of Second-in-Command.",
                        0f
                    )
                    addSpacer(10f)
                    addPara(
                        "To get started, visit the bar of the nearest colony to receive an " +
                        "opportunity to acquire your first Executive Officer.",
                        0f, Misc.getTextColor(), Misc.getHighlightColor(),
                        "bar", "first Executive Officer"
                    )
                }
            )
        }
    }
}
