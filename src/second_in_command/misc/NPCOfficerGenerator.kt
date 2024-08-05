package second_in_command.misc

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.specs.SCBaseAptitudePlugin
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore

object NPCOfficerGenerator {

    fun isBossFleet(fleet: CampaignFleetAPI) : Boolean {
        var flagship = fleet.flagship
        var bossShips = listOf("ziggurat", "tesseract", "hmi_spookyboi_base", "rat_genesis")
        var isBoss = false
        if (flagship != null) {
            if (bossShips.contains(flagship.baseOrModSpec().baseHullId)) isBoss = true
        }
        return isBoss
    }

    fun generateForFleet(data: SCData, fleet: CampaignFleetAPI) {

        generateRandom(data, fleet)

    }


    fun generateRandom(data: SCData, fleet: CampaignFleetAPI) {

        var flagship = fleet.flagship
        var isOmega = fleet.faction.id == Factions.OMEGA
        var hasSupercap = fleet.fleetData.membersListCopy.find { it.deploymentPointsCost >= 70 } != null
        var isAutomated = flagship?.isAutomated() ?: false

        var isBoss = isBossFleet(fleet)

        //var isStation = fleet.isStationMode

        var combatFP = fleet.fleetData.membersListCopy.filter { !it.isCivilian }.sumOf { it.fleetPointCost }.toFloat()
        var nonCombatFP = fleet.fleetData.membersListCopy.filter { it.isCivilian }.sumOf { it.fleetPointCost }.toFloat() * 0.2f
        combatFP += nonCombatFP

        combatFP += 1000f

        if (isBoss) combatFP += 120f
        if (isOmega) combatFP += 80f
        if (hasSupercap) combatFP += 80f
        if (isAutomated) combatFP += 20f
        //if (isStation) combatFP += 40f

        var divide = MathUtils.getRandomNumberInRange(20f, 22f)
        var maxSkillCount = MathUtils.getRandomNumberInRange(15, 15)

        var skillCount = (combatFP / divide).toInt()
        skillCount = MathUtils.clamp(skillCount, 1, maxSkillCount) //Minimum of atleast 1 skill per fleet

        var aptitudeCount = 1
        aptitudeCount = when (skillCount) {
            1 -> 1
            2 -> pickAptitudeCount(1f, 0.25f, 0f)
            3 -> pickAptitudeCount(1f, 0.4f, 0f)
            4 -> pickAptitudeCount(1f, 0.8f, 0.3f)
            5 -> pickAptitudeCount(1f, 1.2f, 0.4f)
            6 -> pickAptitudeCount(0.2f, 1f, 0.5f)
            7 -> pickAptitudeCount(0f, 1f, 0.6f)
            8 -> pickAptitudeCount(0f, 1f, 0.7f)
            9 -> pickAptitudeCount(0f, 1f, 1f)
            10 -> pickAptitudeCount(0f, 0.5f, 1f)
            11 -> pickAptitudeCount(0f, 0.2f, 1f)
            12 -> pickAptitudeCount(0f, 0.1f, 1f)
            13 -> 3
            14 -> 3
            15 -> 3
            else -> 3
        }


        var aptitudePicker = WeightedRandomPicker<SCBaseAptitudePlugin>()
        for (aptitude in SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }) {
            aptitudePicker.add(aptitude, aptitude.getNPCSpawnWeight(data, fleet))
        }

        var aptitudes = ArrayList<SCBaseAptitudePlugin>()
        for (i in 0 until aptitudeCount) {
            aptitudes.add(aptitudePicker.pickAndRemove())
        }

        var officers = ArrayList<SCOfficer>()
        for (aptitude in aptitudes) {
            var officer = SCUtils.createRandomSCOfficer(aptitude.getId(), fleet.faction)

            if (isBoss || isAutomated) {
                officer.person.portraitSprite = flagship.captain.portraitSprite
            }

            officers.add(officer)
        }


        var unlocked = ArrayList<SCBaseSkillPlugin>()

        for (i in 0 until skillCount) {

            var unlockable = WeightedRandomPicker<PotentialPick>()

            for (officer in officers) {


                var aptitude = officer.getAptitudePlugin()

                aptitude.clearSections()
                aptitude.createSections()
                var sections = aptitude.getSections()

                var skillsInAptitude = sections.flatMap { it.getSkills() }
                var unlockedSkillsCount = unlocked.count { skillsInAptitude.contains(it.getId()) }

                if (unlockedSkillsCount >= 5) continue //Dont let it get more than 5 skills

                for (section in sections) {
                    if (unlockedSkillsCount >= section.requiredPreviousSkills) {
                        var skills = section.getSkills()

                        //Skip Section if one of its skills is unlocked and the section doesnt allow for more
                        var canChooseMultiple = section.canChooseMultiple
                        if (!canChooseMultiple && unlocked.map { it.getId() }.any { skills.contains(it) }) {
                            continue
                        }

                        for (skill in skills) {
                            if (!unlocked.map { it.getId() }.contains(skill)) {
                                var plugin = SCSpecStore.getSkillSpec(skill)!!.getPlugin()
                                unlockable.add(PotentialPick(officer, plugin), plugin.getNPCSpawnWeight(fleet))
                            }
                        }
                    }
                }

            }


            var pick = unlockable.pick()
            if (pick != null) {
                pick.officer.addSkill(pick.skill.getId())
                unlocked.add(pick.skill)
            }


        }


        var slotId = 0
        for (officer in officers) {


            officer.activeSkillIDs = officer.activeSkillIDs.sortedBy { SCSpecStore.getSkillSpec(it)!!.order }.toMutableSet()

            data.addOfficerToFleet(officer)
            data.setOfficerInSlot(slotId, officer)

            slotId += 1


        }



    }

    fun pickAptitudeCount(oneWeight: Float, twoWeight: Float, threeWeight: Float) : Int {
        var picker = WeightedRandomPicker<Int>()
        picker.add(1, oneWeight)
        picker.add(2, twoWeight)
        picker.add(3, threeWeight)
        return picker.pick()
    }

    fun generateZiggurat(data: SCData, fleet: CampaignFleetAPI) {

    }

    fun generateOmega(data: SCData, fleet: CampaignFleetAPI) {

    }
}

data class PotentialPick(val officer: SCOfficer, var skill: SCBaseSkillPlugin)