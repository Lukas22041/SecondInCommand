package second_in_command.specs

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import java.awt.Color

data class SCAptitudeSpec(
    var id: String,
    var name: String,
    var categories: List<SCCategorySpec>,
    var spawnWeight: Float,
    var color: Color,
    var tags: List<String>,
    var order: Int,
    var modSpec: ModSpecAPI,
    var pluginPath: String) {

    private var plugin: SCBaseAptitudePlugin? = null

    fun getPlugin() : SCBaseAptitudePlugin {
        if (plugin == null) {
            plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseAptitudePlugin
            plugin!!.spec = this
        }
        return plugin!!
      /*  var plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as SCBaseAptitudePlugin
        plugin.spec = this
        return plugin*/
    }

}