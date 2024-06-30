package second_in_command.skills.test

import second_in_command.specs.SCBaseAptitudePlugin

class TestAptitude : SCBaseAptitudePlugin() {
    override fun getSpawnWeight(): Float {
        return 1f
    }

    override fun getOriginSkillId(): String {
        return ""
    }

    override fun createSections() {

    }
}