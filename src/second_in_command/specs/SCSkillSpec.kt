package second_in_command.specs

import com.fs.starfarer.api.Global

data class SCSkillSpec(
    var id: String,
    var name: String,
    var iconPath: String,
    var pluginPath: String) {

    fun getPlugin() : SCBaseSkillPlugin {
        var plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseSkillPlugin
        plugin.spec = this
        return plugin
    }

}