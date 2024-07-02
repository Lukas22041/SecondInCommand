package second_in_command.skills.test

import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class TestAptitude2 : SCBaseAptitudePlugin() {
    override fun getSpawnWeight(): Float {
        return 1f
    }

    override fun getOriginSkillId(): String {
        return "sc_test_skill11"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(false, 0, "technology1")
        section1.addSkill("sc_test_skill12")
        section1.addSkill("sc_test_skill13")
        section1.addSkill("sc_test_skill14")

        addSection(section1)

        var section2 = SCAptitudeSection(true, 1, "technology2")
        section2.addSkill("sc_test_skill15")
        section2.addSkill("sc_test_skill16")
        section2.addSkill("sc_test_skill17")
        addSection(section2)

        var section3 = SCAptitudeSection(true, 3, "technology3")
        section3.addSkill("sc_test_skill18")
        section3.addSkill("sc_test_skill19")
        section3.addSkill("sc_test_skill20")
        addSection(section3)
    }
}