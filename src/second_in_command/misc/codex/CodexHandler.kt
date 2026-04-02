package second_in_command.misc.codex

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.impl.SharedUnlockData
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.codex.*
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaCodex.LunaCodex
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaUI.elements.LunaSpriteElement
import second_in_command.SCData
import second_in_command.misc.getAndLoadSprite
import second_in_command.specs.SCAptitudeSpec
import second_in_command.specs.SCSpecStore
import second_in_command.ui.elements.*
import kotlin.math.max
import kotlin.math.min

object CodexHandler {

    var APTITUDE_SET = "sc_aptitudes"

    var blacklist = listOf(
        "tactical_drills",
        "coordinated_maneuvers",
        "wolfpack_tactics",
        "crew_training",
        "fighter_uplink",
        "carrier_group",
        "officer_training",
        "officer_management",
        "best_of_the_best",
        "support_doctrine",
        "force_concentration",
        "navigation",
        "sensors",
        "electronic_warfare",
        "flux_regulation",
        "cybernetic_augmentation",
        "phase_corps",
        "neural_link",
        "automated_ships",
        "bulk_transport",
        "salvaging",
        "field_repairs",
        "containment_procedures",
        "makeshift_equipment",
        "industrial_planning",
        "derelict_contingent",
        "hull_restoration",
    )

    fun reportPlayerAwareOfThing(thingId: String?, setId: String?, codexEntryId: String?, withSave: Boolean): Boolean {
        var shared = SharedUnlockData.get()
        val wasLocked: Boolean = shared.isEntryLocked(codexEntryId)
        if (shared.addToSet(setId, thingId)) {
            if (wasLocked && !shared.isEntryLocked(codexEntryId)) CodexIntelAdder.get().addEntry(codexEntryId)
            if (withSave) shared.saveIfNeeded()
            return true
        }
        return false
    }


    fun onAboutToStartGeneratingCodex() {

        for (skillId in blacklist) {
            var spec = Global.getSettings().getSkillSpec(skillId)
            spec.addTag("hide_in_codex")
        }
    }

    fun onAboutToLinkCodexEntries() {

    }

    fun onCodexDataGenerated() {

        addModEntry()

        generateAptitudeEntries()


        //var relatedAptitudes = ArrayList<String>()

        //relatedAptitudes.add(LunaCodex.getModEntryId("second_in_command"))
        for (aptitude in SCSpecStore.getAptitudeSpecs()) {
           // relatedAptitudes.add(getAptitudEntryId(aptitude.id))

            CodexDataV2.makeRelated(LunaCodex.getModEntryId("second_in_command"), getAptitudEntryId(aptitude.id))

            for (other in SCSpecStore.getAptitudeSpecs()) {
                if (aptitude.categories.any { other.categories.contains(it) }) {
                    CodexDataV2.makeRelated(getAptitudEntryId(aptitude.id), getAptitudEntryId(other.id))
                }
            }
        }

        //CodexDataV2.makeRelated(*relatedAptitudes.toTypedArray())


      /*  var relateds = ArrayList<String>()
        //relateds.add(CodexDataV2.CAT_SKILLS) //Doesnt actually move you to the category sadly
        relateds.add(CodexDataV2.getSkillEntryId("helmsmanship"))
        relateds.add(CodexDataV2.getSkillEntryId("combat_endurance"))
        relateds.add(CodexDataV2.getSkillEntryId("impact_mitigation"))
        relateds.add(CodexDataV2.getSkillEntryId("damage_control"))
        relateds.add(CodexDataV2.getSkillEntryId("field_modulation"))
        relateds.add(CodexDataV2.getSkillEntryId("point_defense"))
        relateds.add(CodexDataV2.getSkillEntryId("target_analysis"))
        relateds.add(CodexDataV2.getSkillEntryId("ballistic_mastery"))
        relateds.add(CodexDataV2.getSkillEntryId("systems_expertise"))
        relateds.add(CodexDataV2.getSkillEntryId("missile_specialization"))
        relateds.add(CodexDataV2.getSkillEntryId("energy_weapon_mastery"))
        relateds.add(CodexDataV2.getSkillEntryId("gunnery_implants"))
        relateds.add(CodexDataV2.getSkillEntryId("ordnance_expert"))
        relateds.add(CodexDataV2.getSkillEntryId("polarized_armor"))

        for (related in relateds) {
            CodexDataV2.makeRelated(LunaCodex.getModEntryId("second_in_command"), related)
        }*/


    }

    fun addModEntry() {
        var cat = LunaCodex.getModsCategory()

        var path = "graphics/secondInCommand/codex_icon.png"
        Global.getSettings().loadTexture(path)
        var modEntry = ModEntry(LunaCodex.getModEntryId("second_in_command"), "Second-in-Command", path)

        cat.addChild(modEntry)
        CodexDataV2.ENTRIES.put(modEntry.id, modEntry)


    }

    fun getAptitudEntryId(aptitudeId: String) : String {
        return "codex_sic_aptitude_$aptitudeId"
    }

    fun generateAptitudeEntries() {
        var skillsCategory = CodexDataV2.ROOT.children.find { it.id == CodexDataV2.CAT_SKILLS }

        var children = ArrayList(skillsCategory!!.children)
        for (child in children) {
            skillsCategory.children.remove(child)
        }

        for (aptitude in SCSpecStore.getAptitudeSpecs().sortedBy { it.order }) {
            var plugin = aptitude.getPlugin()

            if (aptitude.tags.contains("hide_in_codex")) continue

            var aptitudeEntry = AptitudeEntry(aptitude, getAptitudEntryId(plugin.id), plugin.name, plugin.originSkillPlugin.iconPath)

            if (!aptitude.tags.contains("startingOption") && !aptitude.tags.contains("always_show_in_codex")) {
                aptitudeEntry.addTag(Tags.CODEX_UNLOCKABLE)
            }

            skillsCategory!!.addChild(aptitudeEntry)
            CodexDataV2.ENTRIES.put(aptitudeEntry.id, aptitudeEntry)
        }

        for (child in children) {
            skillsCategory.addChild(child)
        }

        //CodexDataV2.makeRelated()
    }

}






//Aptitude Entries

class AptitudeEntry(var aptitudeSpec: SCAptitudeSpec, id: String, title: String, icon: String) : CodexEntryV2(id,  title, icon) {
    override fun isCategory(): Boolean {
        return false
    }

    override fun getSourceMod(): ModSpecAPI? {
        return aptitudeSpec.modSpec
    }

    override fun createTitleForList(info: TooltipMakerAPI?, width: Float, mode: CodexEntryPlugin.ListMode?) {
        info!!.addPara(title, aptitudeSpec.color, 0f)
        info.addPara("Aptitude", Misc.getGrayColor(), 0f)
    }

    override fun hasCustomDetailPanel(): Boolean {
        return true
    }

    override fun isUnlockedIfRequiresUnlock(): Boolean {
        return SharedUnlockData.get().getSet(CodexHandler.APTITUDE_SET).contains(aptitudeSpec.id)
    }

    override fun getUnlockRelatedTags(): MutableSet<String> {
        return tags
    }

    override fun createCustomDetail(panel: CustomPanelAPI?, relatedEntries: UIPanelAPI?, codex: CodexDialogAPI?) {

        var fleet = Global.getFactory().createEmptyFleet(Global.getSettings().createBaseFaction(Factions.NEUTRAL), false)
        var data = SCData(fleet)

        val width = panel!!.position.width


        // the right width for a tooltip wrapped in a box to fit next to relatedEntries
        // 290 is the width of the related entries widget, but it may be null
        val tw = width - 290f - 10f - 10f


        val tooltip = panel!!.createUIElement(tw, 700f, false)
        panel.addUIElement(tooltip)


        //Start

        var subpanel = Global.getSettings().createCustom(tw, 84f, null)
        subpanel.position.inTL(0f, 0f)
        panel.addComponent(subpanel)
        var subelement = subpanel.createUIElement(tw, 84f, false)
        subpanel.addUIElement(subelement)
        tooltip.addSpacer(84f)



        var aptitudePlugin = aptitudeSpec.getPlugin()

        val skillBar = AptitudeSkillBarElement(
            aptitudePlugin = aptitudePlugin,
            data = data,
            officer = null,
            parentElement = subelement,
            skillSize = 64f,
            backgroundPositioner = { bg ->
                bg.elementPanel.position.inTL(0f, subpanel.position.height / 2)
            }
        )

        tooltip.addSpacer(16f)

        //End

        tooltip.addSectionHeading("Description", Alignment.MID, 0f)
        tooltip.addSpacer(10f)

        aptitudePlugin.addCodexDescription(tooltip)



        var categories = aptitudePlugin.categories

        if (categories.isNotEmpty()) {
            var categoryNames = ArrayList<String>()
            var categoryText = ""

            for (category in categories) {
                categoryNames.add(category.name)

                categoryText += "${category.name}, "
            }

            categoryText = categoryText.trim()
            categoryText = categoryText.trim { it == ',' }

            var extraS = "y"
            if (categories.size >= 2) extraS = "ies"

            tooltip.addSpacer(10f)
            tooltip.addPara("This aptitude is part of the $categoryText Categor$extraS. As such it can not be used with aptitudes of the same category.", 0f, Misc.getGrayColor(), aptitudePlugin.color, "$categoryText")
        }




        if (relatedEntries != null) {
            panel!!.addComponent(relatedEntries).inTR(+10f, 100f)
        }

        var height: Float = tooltip.getPosition().getHeight()
        if (relatedEntries != null) {
            height = max(height.toDouble(), relatedEntries.position.height.toDouble()).toFloat()
        }

        tooltip.position.setSize(tooltip.position.width, height)
        panel!!.position.setSize(width, height)


    }

    /*override fun getSourceMod(): ModSpecAPI {
        return Global.getSettings().modManager.getModSpec("second_in_command")
    }*/
}

// Second-in-Command mod Entry

class ModEntry(id: String, title: String, icon: String) : CodexEntryV2(id,  title, icon) {
    override fun isCategory(): Boolean {
        return false
    }

    override fun createTitleForList(info: TooltipMakerAPI?, width: Float, mode: CodexEntryPlugin.ListMode?) {
        info!!.addPara(title, Misc.getBasePlayerColor(), 0f)
        info.addPara("Mod", Misc.getGrayColor(), 0f)
    }

    override fun hasCustomDetailPanel(): Boolean {
        return true
    }

    override fun createCustomDetail(panel: CustomPanelAPI?, relatedEntries: UIPanelAPI?, codex: CodexDialogAPI?) {



        val width = panel!!.position.width




        // the right width for a tooltip wrapped in a box to fit next to relatedEntries
        // 290 is the width of the related entries widget, but it may be null
        val tw = width - 290f - 10f


        val tooltip = panel!!.createUIElement(tw, 700f, false)
        panel.addUIElement(tooltip)

        tooltip.addSectionHeading("Description", Alignment.MID, 0f)
        tooltip.addSpacer(10f)


        tooltip.addPara("Second-in-Command is a mod that reworks the games skill system. " +
                "Within the vanilla system, you pick between Combat, Leadership, Technology and Industry aptitudes. ",0f, Misc.getTextColor(), Misc.getHighlightColor(),
            "Combat", "Leadership", "Technology", "Industry")
        tooltip.addSpacer(10f)

        tooltip.addPara("In this mod, only the Combat aptitude remains. The other aptitudes have been replaced with slots for a new class of crew called Executive Officers. " +
                "An executive officer has an aptitude they excel at, when slotted in to one of the three available slots, they fill it with their aptitudes skills.",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "Combat", "Executive Officers", "three available slots", "skills")
        tooltip.addSpacer(10f)

        var path = "graphics/secondInCommand/codex/codex_example.png"
        var sprite = Global.getSettings().getAndLoadSprite(path)

        var spriteWidth = sprite.width
        var spriteHeight = sprite.height

        var scale = min((tw - 10f) / spriteWidth, 10000 / spriteHeight)

        tooltip.addLunaSpriteElement(path, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, spriteWidth * scale, spriteHeight * scale)

        tooltip.addSpacer(10f)
        tooltip.addPara("Executive officers level independently of the player, and have a maximum level of 5. By default they have one skill unlocked, and they can learn 5 additional skills. You can freely swap out an officer whenever you want, as long as you are close to a colony.",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "level independently", "5", "5")
        tooltip.addSpacer(10f)

        tooltip.addPara("They can be commonly found in the comm-directory of colonies, but can also appear in cryo-pods from salvaged wrecks. ",
            0f, Misc.getTextColor(), Misc.getHighlightColor(), "")

        if (relatedEntries != null) {
            panel!!.addComponent(relatedEntries).inTR(0f, 0f)
        }

        var height: Float = tooltip.getPosition().getHeight()
        if (relatedEntries != null) {
            height = max(height.toDouble(), relatedEntries.position.height.toDouble()).toFloat()
        }

        tooltip.position.setSize(tooltip.position.width, height)
        panel!!.position.setSize(width, height)


    }

    /*override fun getSourceMod(): ModSpecAPI {
        return Global.getSettings().modManager.getModSpec("second_in_command")
    }*/
}