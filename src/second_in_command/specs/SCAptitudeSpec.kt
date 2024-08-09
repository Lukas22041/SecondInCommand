package second_in_command.specs

import com.fs.starfarer.api.Global
import java.awt.Color

data class SCAptitudeSpec(
    var id: String,
    var name: String,
    var categories: List<SCCategorySpec>,
    var spawnWeight: Float,
    var color: Color,
    var tags: List<String>,
    var pluginPath: String) {

    fun getPlugin() : SCBaseAptitudePlugin {
        var plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseAptitudePlugin
        plugin.spec = this
        return plugin
    }

}