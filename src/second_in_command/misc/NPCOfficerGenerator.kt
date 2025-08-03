package second_in_command.misc

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.lazywizard.lazylib.MathUtils
import org.magiclib.kotlin.getSalvageSeed
import org.magiclib.kotlin.getSourceMarket
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.SCUtils
import second_in_command.specs.SCBaseAptitudePlugin
import second_in_command.specs.SCBaseSkillPlugin
import second_in_command.specs.SCOfficer
import second_in_command.specs.SCSpecStore
import java.util.*

object NPCOfficerGenerator {

    //Fleet key, sets how many skills should be generated for this fleet manualy
    var SKILL_COUNT_OVERWRITE_KEY = "\$sic_skill_gen_overwrite"

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

        //var seed = 0L

       // var fleetSeed = fleet.getMemoryWithoutUpdate().getLong(MemFlags.SALVAGE_SEED) as Long?

       /* if (fleetSeed == 0L) {
            if (fleet.getSourceMarket() != null) {
                var marketSeed = fleet.getSourceMarket()?.primaryEntity?.getSalvageSeed()
                if (marketSeed != null) {
                    seed = marketSeed
                }
            }
        }*/

       /* if (seed == 0L) {
            seed = fleet.getSalvageSeed() //Force it to generate the salvage seed if other options werent sucessful

        }*/

    /*    val hash = fleet.id.hashCode()*/

        //var seed = fleet.getSalvageSeed()




        var sSeed = Global.getSector().seedString

        var seed = (fleet.id + sSeed).hashCode().toLong()

        //Required to keep the skills from defense fleets consistent
        //Instead of grabbing the fleets salvage seed, which is re-randomised each time you interact, grab the interaction targets
        //Use ID of entities instead of salvage seeds, combined with sector seed so that fleets in different playthroughs have different skills.
        var playerLoc = Global.getSector().currentLocation
        for (entity in playerLoc.allEntities) {
            var defFleet = entity.memoryWithoutUpdate.getFleet("\$defenderFleet")
            if (defFleet != null) {
                if (defFleet == fleet) {
                    //seed = entity.getSalvageSeed()
                    seed = (entity.id + sSeed).hashCode().toLong()
                    break;
                }
            }
        }

        var random = Random(seed)

        var flagship = fleet.flagship
        var isOmega = fleet.faction.id == Factions.OMEGA
        var hasSupercap = fleet.fleetData.membersListCopy.find { it.deploymentPointsCost >= 70 } != null
        var isAutomated = flagship?.isAutomated() ?: false

        var isBoss = isBossFleet(fleet)

        //var isStation = fleet.isStationMode

        var combatFP = fleet.fleetData.membersListCopy.filter { !it.isCivilian }.sumOf { it.fleetPointCost }.toFloat()
        var nonCombatFP = fleet.fleetData.membersListCopy.filter { it.isCivilian }.sumOf { it.fleetPointCost }.toFloat() * 0.2f
        combatFP += nonCombatFP

        if (isBoss) combatFP += 120f
        if (isOmega) combatFP += 80f
        if (hasSupercap) combatFP += 80f
        if (isAutomated) combatFP += 10f
        //if (isStation) combatFP += 40f

        var minSkills = 11
        var maxSkills = 14

        if (SCSettings.difficulty == "Hard") {
            minSkills = 15
            maxSkills = 15

            combatFP *= 1.25f
            combatFP += 50
        }
        else if (SCSettings.difficulty == "Easy") {
            minSkills = 8
            maxSkills = 12

            combatFP *= 0.666f
        }


        var divide = getRandomNumberInRange(random, 20f, 24f)
        var maxSkillCount = getRandomNumberInRange(random, minSkills, maxSkills)

        var skillCount = (combatFP / divide).toInt()
        skillCount = MathUtils.clamp(skillCount, 1, maxSkillCount) //Minimum of atleast 1 skill per fleet

        //Overwrite skill count if available
        var skillCountOverwrite = fleet.memoryWithoutUpdate.get(SKILL_COUNT_OVERWRITE_KEY)
        if (skillCountOverwrite is Int) {
            skillCount = skillCountOverwrite
        }

        var aptitudeCount = 1
        aptitudeCount = when (skillCount) {
            1 -> 1
            2 -> pickAptitudeCount(random, 1f, 0.25f, 0f)
            3 -> pickAptitudeCount(random, 1f, 0.4f, 0f)
            4 -> pickAptitudeCount(random, 1f, 0.8f, 0.3f)
            5 -> pickAptitudeCount(random, 1f, 1.2f, 0.4f)
            6 -> pickAptitudeCount(random, 0.2f, 1f, 0.5f)
            7 -> pickAptitudeCount(random, 0f, 1f, 0.6f)
            8 -> pickAptitudeCount(random, 0f, 1f, 0.7f)
            9 -> pickAptitudeCount(random, 0f, 1f, 1f)
            10 -> pickAptitudeCount(random, 0f, 0.5f, 1f)
            11 -> pickAptitudeCount(random, 0f, 0.2f, 1f)
            12 -> pickAptitudeCount(random, 0f, 0.1f, 1f)
            13 -> 3
            14 -> 3
            15 -> 3
            else -> 3
        }
        if (SCSettings.enable4thSlot && random.nextFloat() >= 0.15) {
            if (aptitudeCount == 1) skillCount += 1
            if (aptitudeCount == 2) skillCount += MathUtils.getRandomNumberInRange(1,2)
            if (aptitudeCount == 3) skillCount += MathUtils.getRandomNumberInRange(2,4)

            maxSkillCount += 4
            if (SCSettings.difficulty == "Hard") maxSkillCount += 1

            skillCount = MathUtils.clamp(skillCount, 1, maxSkillCount) //Minimum of atleast 1 skill per fleet

            aptitudeCount += 1
        }



        var aptitudePicker = WeightedRandomPicker<SCBaseAptitudePlugin>()
        aptitudePicker.random = random

        var aptitudes = ArrayList<SCBaseAptitudePlugin>()


        var availableAptitudes = SCSpecStore.getAptitudeSpecs().map { it.getPlugin() }.toMutableList()

        var priority = availableAptitudes.filter { it.guaranteePick(fleet) }.toMutableList()


        while (priority.isNotEmpty() && aptitudeCount >= 1) {

            var aptitude = priority.first()

            aptitudes.add(aptitude)
            priority.remove(aptitude)

            aptitudeCount -= 1

            var categories = aptitude.categories
            for (other in ArrayList(availableAptitudes)) {
                var otherCategories = other.categories

                if (categories.any { otherCategories.contains(it) }) {
                    availableAptitudes.remove(other)
                    priority.remove(other)
                }
            }

        }


        var noPriority = availableAptitudes.filter { !it.guaranteePick(fleet) }.toMutableList()
        for (aptitude in noPriority) {
            aptitudePicker.add(aptitude, aptitude.getNPCFleetSpawnWeight(data, fleet))
        }

        for (i in 0 until aptitudeCount) {
            if (aptitudeCount <= 0) break
            if (aptitudePicker.isEmpty) break

            var pick = aptitudePicker.pickAndRemove()
            aptitudes.add(pick)

            var categories = pick.categories
            for (other in ArrayList(noPriority)) {
                var otherCategories = other.categories

                if (categories.any { otherCategories.contains(it) }) {
                    noPriority.remove(other)
                    aptitudePicker.remove(other)
                }
            }
        }

        var officers = ArrayList<SCOfficer>()
        for (aptitude in aptitudes) {
            var officer = SCUtils.createRandomSCOfficer(aptitude.getId(), fleet.faction, random)

            if (isBoss || isAutomated) {
                officer.person.portraitSprite = flagship.captain.portraitSprite
            }

            officers.add(officer)
        }


        var unlocked = ArrayList<SCBaseSkillPlugin>()

        for (i in 0 until skillCount) {

            var unlockable = WeightedRandomPicker<PotentialPick>()
            unlockable.random = random

            for (officer in officers) {


                var aptitude = officer.getAptitudePlugin()

                /*aptitude.clearSections()
                aptitude.createSections()*/
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

    fun pickAptitudeCount(random: Random, oneWeight: Float, twoWeight: Float, threeWeight: Float) : Int {
        var picker = WeightedRandomPicker<Int>()
        picker.random = random

        picker.add(1, oneWeight)
        picker.add(2, twoWeight)
        picker.add(3, threeWeight)
        return picker.pick()
    }

    fun generateZiggurat(data: SCData, fleet: CampaignFleetAPI) {

    }

    fun generateOmega(data: SCData, fleet: CampaignFleetAPI) {

    }

    fun getRandomNumberInRange(random: Random, min: Float, max: Float) : Float {
        return random.nextFloat() * (max - min) + min
    }

    fun getRandomNumberInRange(random: Random, min: Int, max: Int) : Int {
        return if (min >= max) {
            if (min == max) {
                min
            } else random.nextInt(min - max + 1) + max
        } else random.nextInt(max - min + 1) + min

    }
}

data class PotentialPick(val officer: SCOfficer, var skill: SCBaseSkillPlugin)