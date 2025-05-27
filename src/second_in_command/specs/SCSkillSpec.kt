package second_in_command.specs

import com.fs.starfarer.api.Global

data class SCSkillSpec(
    var id: String,
    var name: String,
    var iconPath: String,
    var npcSpawnWeight: Float,
    var order: Int,
    var modname: String,
    var pluginPath: String) {

    private var plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseSkillPlugin

    init {
        plugin.spec = this
    }

    fun getPlugin() : SCBaseSkillPlugin {
        /*var plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseSkillPlugin
        plugin.spec = this*/
        return plugin
    }

}