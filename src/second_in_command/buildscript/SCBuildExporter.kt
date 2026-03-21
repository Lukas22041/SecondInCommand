package second_in_command.buildscript

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import second_in_command.misc.ReflectionUtils

object SCBuildExporter {

    private val logger = Global.getLogger(SCBuildExporter::class.java).apply { level = Level.ALL }

    // Load file-related classes through the bootstrap classloader to bypass script restrictions
    private val fileClass = Class.forName("java.io.File", false, Class::class.java.classLoader)
    private val fisClass = Class.forName("java.io.FileInputStream", false, Class::class.java.classLoader)
    private val fosClass = Class.forName("java.io.FileOutputStream", false, Class::class.java.classLoader)

    /**
     * Write the JSON export and copy referenced assets.
     * Output goes to: <mod_root>/build_export/
     */
    fun export(data: BuildExportData, spritePaths: Set<String>, spriteRenameMap: Map<String, String> = emptyMap()) {
        val modPath = Global.getSettings().modManager.getModSpec("second_in_command").path
        val exportDir = newFile(modPath, "build_export")
        val assetsDir = newFile(exportDir, "assets")

        ReflectionUtils.invoke("mkdirs", exportDir)
        ReflectionUtils.invoke("mkdirs", assetsDir)

        // Write JSON
        val jsonFile = newFile(exportDir, "api.json")
        val jsonString = data.toJSON().toString(2)
        writeBytes(jsonFile, jsonString.toByteArray(Charsets.UTF_8))
        logger.info("SC Build: Wrote api.json (${jsonString.length} chars)")

        // Copy assets
        var copied = 0
        for (spritePath in spritePaths) {
            if (spritePath.isEmpty()) continue
            try {
                // Use rename map if available (handles duplicate filename collisions), else fall back
                val assetName = spriteRenameMap[spritePath] ?: TooltipElementParser.spriteToAssetName(spritePath)
                val destFile = newFile(assetsDir, assetName)

                val sourceFile = findSpriteFile(spritePath, modPath)
                if (sourceFile != null && fileExists(sourceFile)) {
                    copyFile(sourceFile, destFile)
                    copied++
                } else {
                    logger.warn("SC Build: Could not find sprite file for: $spritePath")
                }
            } catch (e: Exception) {
                logger.warn("SC Build: Failed to copy asset $spritePath: ${e.message}")
            }
        }

        logger.info("SC Build: Copied $copied assets to build_export/assets/")
        logger.info("SC Build: Export complete at ${ReflectionUtils.invoke("getAbsolutePath", exportDir)}")
    }

    /** Create a File(parent, child) via reflection */
    private fun newFile(parent: Any, child: String): Any {
        return ReflectionUtils.instantiate(fileClass, parent, child)!!
    }

    /** Create a File(path) via reflection */
    private fun newFile(path: String): Any {
        return ReflectionUtils.instantiate(fileClass, path)!!
    }

    /** Check File.exists() via reflection */
    private fun fileExists(file: Any): Boolean {
        return ReflectionUtils.invoke("exists", file) as Boolean
    }

    /** Write bytes to a file via reflection */
    private fun writeBytes(file: Any, bytes: ByteArray) {
        val fos = ReflectionUtils.instantiate(fosClass, file)!!
        try {
            ReflectionUtils.invoke("write", fos, bytes)
        } finally {
            ReflectionUtils.invoke("close", fos)
        }
    }

    /** Copy a file by reading all bytes and writing them */
    private fun copyFile(source: Any, dest: Any) {
        val fis = ReflectionUtils.instantiate(fisClass, source)!!
        try {
            val bytes = ReflectionUtils.invoke("readAllBytes", fis) as ByteArray
            writeBytes(dest, bytes)
        } finally {
            ReflectionUtils.invoke("close", fis)
        }
    }

    /**
     * Find the actual file for a sprite path. Sprites can come from:
     * 1. The mod's own directory
     * 2. Other mod directories
     * 3. The base game (starsector-core)
     */
    private fun findSpriteFile(spritePath: String, modPath: String): Any? {
        val normalized = spritePath.replace("\\", "/")

        // Check the current mod first
        val inMod = newFile(modPath, normalized)
        if (fileExists(inMod)) return inMod

        // Check all enabled mods
        for (mod in Global.getSettings().modManager.enabledModsCopy) {
            val inOtherMod = newFile(mod.path, normalized)
            if (fileExists(inOtherMod)) return inOtherMod
        }

        // Check starsector-core
        val modDir = newFile(modPath)
        val rootDir = ReflectionUtils.invoke("getParentFile", modDir)!! // mods/
        val starsectorRoot = ReflectionUtils.invoke("getParentFile", rootDir)!! // starsector root
        val inCore = newFile(starsectorRoot, "starsector-core/$normalized")
        if (fileExists(inCore)) return inCore

        return null
    }
}
