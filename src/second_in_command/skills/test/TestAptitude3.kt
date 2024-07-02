package second_in_command.skills.test

import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class TestAptitude3 : SCBaseAptitudePlugin() {
    override fun getSpawnWeight(): Float {
        return 1f
    }

    override fun getOriginSkillId(): String {
        return "sc_test_skill21"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "technology1")
        section1.addSkill("sc_test_skill22")
        section1.addSkill("sc_test_skill23")
        section1.addSkill("sc_test_skill24")
        section1.addSkill("sc_test_skill25")
        section1.addSkill("sc_test_skill26")

        addSection(section1)

        var section2 = SCAptitudeSection(true, 3, "technology2")
        section2.addSkill("sc_test_skill27")
        section2.addSkill("sc_test_skill28")

        addSection(section2)

        var section3 = SCAptitudeSection(true, 4, "technology3")
        section3.addSkill("sc_test_skill29")
        section3.addSkill("sc_test_skill30")
        addSection(section3)
    }
}