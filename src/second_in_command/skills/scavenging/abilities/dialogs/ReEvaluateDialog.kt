package second_in_command.skills.scavenging.abilities.dialogs

import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec.DropData
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity
import second_in_command.SCUtils
import second_in_command.misc.levelBetween

class ReEvaluateDialog : InteractionDialogPlugin {
    override fun init(dialog: InteractionDialogAPI) {

        var data = SCUtils.getPlayerData()
        var scrapManager = data.scrapManager
        var random = scrapManager.reEvaluateRandom

        dialog?.promptText = ""

        var rng = random.nextFloat()
        var multStrength = 0.8f
        if (rng >= 0.90f) multStrength = 2f
        else if (rng >= 0.80f) multStrength = 1.5f
        else if (rng >= 0.60f) multStrength = 1.25f
        else if (rng >= 0.30f) multStrength = 1f
        else multStrength = 0.75f
        val overallMult = 1f * multStrength

        val dropRandom: MutableList<DropData> = ArrayList()
        val dropValue: MutableList<DropData> = ArrayList()

        var valueMult = 1f
        var fp = data.fleet.fleetPoints
        var fpMult = fp.toFloat().levelBetween(0f, 240f)
        valueMult += fpMult

        var d = DropData()
        d.chances = 1
        d.group = "blueprints_low"
        dropRandom.add(d)

        d = DropData()
        d.chances = 2
        d.group = "any_hullmod_low"
        dropRandom.add(d)

        d = DropData()
        d.chances = 1
        d.group = "package_bp"
        d.valueMult = 0.1f
        dropRandom.add(d)

        d = DropData()
        d.chances = 7
        d.group = "weapons1"
        dropRandom.add(d)

        d = DropData()
        d.chances = 2
        d.group = "weapons2"
        dropRandom.add(d)

        d = DropData()
        //d.chances = 100;
        d.group = "basic"
        d.value = 7500
        dropValue.add(d)

        val result = SalvageEntity.generateSalvage(random, valueMult, 1f, overallMult, 1f, dropValue, dropRandom)

        dialog.visualPanel.showLoot("Salvaged", result, false, true, true) {
            dialog.dismiss()
            dialog.hideTextPanel()
            dialog.hideVisualPanel()
        }


    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI> {
        return hashMapOf()
    }
}