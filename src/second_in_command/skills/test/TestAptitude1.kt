package second_in_command.skills.test

import second_in_command.specs.SCAptitudeSection
import second_in_command.specs.SCBaseAptitudePlugin

class TestAptitude1 : SCBaseAptitudePlugin() {
    override fun getSpawnWeight(): Float {
        return 1f
    }

    override fun getOriginSkillId(): String {
        return "sc_test_skill1"
    }

    override fun createSections() {

        var section1 = SCAptitudeSection(true, 0, "leadership1")
        section1.addSkill("sc_test_skill2")
        section1.addSkill("sc_test_skill3")
        section1.addSkill("sc_test_skill4")
        section1.addSkill("sc_test_skill5")
        addSection(section1)

        var section2 = SCAptitudeSection(true, 2, "leadership2")
        section2.addSkill("sc_test_skill6")
        section2.addSkill("sc_test_skill7")
        section2.addSkill("sc_test_skill8")
        addSection(section2)

        var section3 = SCAptitudeSection(false, 3, "leadership3")
        section3.addSkill("sc_test_skill9")
        section3.addSkill("sc_test_skill10")
        addSection(section3)
    }
}