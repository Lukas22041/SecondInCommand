package second_in_command.specs

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import org.lazywizard.lazylib.ext.json.getFloat
import second_in_command.SCUtils
import second_in_command.misc.loadTextureCached
import java.awt.Color

object SCSpecStore {

    var logger = Global.getLogger(this::class.java).apply { level = Level.ALL }

    private var aptitudeSpecs = ArrayList<SCAptitudeSpec>()
    private var aptitudeSpecsMap = HashMap<String, SCAptitudeSpec>()
    @JvmStatic
    fun getAptitudeSpecs() = aptitudeSpecs
    @JvmStatic
    fun getAptitudeSpec(specId: String) = aptitudeSpecsMap.get(specId)

    //private var skillSpecs = ArrayList<SCSkillSpec>()
    private var skillSpecs = HashMap<String, SCSkillSpec>()
    @JvmStatic
    fun getSkillSpecs() = skillSpecs
    @JvmStatic
    fun getSkillSpec(specId: String) = skillSpecs.get(specId)


    private var categorySpecs = ArrayList<SCCategorySpec>()
    @JvmStatic
    fun getCategorySpecs() = categorySpecs
    fun getCategorySpec(specId: String) = categorySpecs.find { it.id == specId }


    fun loadCategoriesFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/secondInCommand/SCCategories.csv", SCUtils.MOD_ID)

        for (index in 0 until  CSV.length())
        {
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")
            //val category = row.getString("category")

            val colorString = row.getString("color")
            val cs = colorString.split(",").map { it.trim().toInt() }
            val color = Color(cs[0], cs[1], cs[2], cs[3])

            var spec = SCCategorySpec(id, name, color)
            categorySpecs.add(spec)
        }

        logger.debug("Second in Command: Loaded ${categorySpecs.count()} Category Specs.")
    }

    fun loadAptitudeSpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/secondInCommand/SCAptitudes.csv", SCUtils.MOD_ID)

        var order = 0
        for (index in 0 until  CSV.length())
        {
            order++
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")
            //val category = row.getString("category")

            var categories = mutableListOf<SCCategorySpec>()
            val categoriesString = row.getString("categories").trim()
            if (categoriesString != "") {
                var list = categoriesString.split(",").map { it.trim() }
                for (entry in list) {
                    var spec = getCategorySpec(entry)
                    categories.add(spec!!)
                }
            }


            val spawnWeight = row.getFloat("spawnWeight")

            var specsOrder = row.optInt("order", order)

            val colorString = row.getString("color")
            val cs = colorString.split(",").map { it.trim().toInt() }
            val color = Color(cs[0], cs[1], cs[2], cs[3])

            var tags = row.getString("tags").split(",").map { it.trim() }

            var modName = filterModPath(row.getString("fs_rowSource"))
            var modSpec = Global.getSettings().modManager.enabledModsCopy.find { it.dirName == modName }!!

            val pluginPath = row.getString("plugin")

            var spec = SCAptitudeSpec(id, name, categories, spawnWeight, color, tags, specsOrder, modSpec, pluginPath)
            aptitudeSpecs.add(spec)
            aptitudeSpecsMap.put(id, spec)
        }

        logger.debug("Second in Command: Loaded ${aptitudeSpecs.count()} Aptitude Specs.")
    }

    fun loadSkillSpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/secondInCommand/SCSkills.csv", SCUtils.MOD_ID)

        var order = 0
        for (index in 0 until  CSV.length())
        {
            order++
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")
            val iconPath = row.getString("iconPath")
            Global.getSettings().loadTextureCached(iconPath)

            val npcSpawnWeightString = row.getString("npcSpawnWeight")
            var npcSpawnWeight = 0f
            if (npcSpawnWeightString != "") npcSpawnWeight = npcSpawnWeightString.toFloat()

            var modName = filterModPath(row.getString("fs_rowSource"))

            val pluginPath = row.getString("plugin")

            var spec = SCSkillSpec(id, name, iconPath, npcSpawnWeight, order, modName, pluginPath)
            skillSpecs.put(id, spec)
        }

        logger.debug("Second in Command: Loaded ${skillSpecs.count()} Skill Specs.")
    }



    //From Console Commands by LazyWizard
    private fun filterModPath(fullPath: String): String {
        var modPath = fullPath.replace("/", "\\")
        modPath = modPath.substring(modPath.lastIndexOf("\\mods\\"))
        modPath = modPath.substring(0, modPath.indexOf('\\', 6)) + "\\"
        modPath = modPath.replace("\\mods\\", "")
        modPath = modPath.replace("\\", "")
        return modPath
    }


}