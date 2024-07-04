package second_in_command.misc;

import com.fs.starfarer.api.Global;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//From:
//https://github.com/scardwell15/takenoprisoners/blob/main/src/takenoprisoners/utils/SkillsUtil.java
public class VanillaSkillsUtil {
    private static String PATH = "data/characters/skills/";
    private static final Map<String, List<List<Object>>> skillEffectsMap = new HashMap<>();

    /**
     * @param skillId
     * @return can implement either LevelBasedEffect OR DescriptionSkillEffect.
     */
    public static List<List<Object>> getLevelEffects(String skillId) {
        if (skillEffectsMap.containsKey(skillId)) {
            return skillEffectsMap.get(skillId);
        }

        List<JSONObject> effectObjs = getEffectGroups(skillId);
        List<List<Object>> effectLevelList = new ArrayList<>();
        for (int i = 0; i < effectObjs.size(); i++) {
            JSONObject effectGroup = effectObjs.get(i);
            List<Object> effects = createEffects(effectGroup);
            effectLevelList.add(effects);
        }

        skillEffectsMap.put(skillId, effectLevelList);
        return effectLevelList;
    }

    private static JSONObject readJson(String skillId) {
        try {
            JSONObject json = Global.getSettings().loadJSON(PATH + skillId + ".skill");
            //JSONObject json = Global.getSettings().getMergedJSONForMod(PATH + skillId + ".skill", "starsector-core");
            return json;
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Missing/bad skill " + skillId, e);
        }
    }

    private static List<JSONObject> getEffectGroups(String skillId) {
        JSONObject json = readJson(skillId);

        try {
            JSONArray array = json.getJSONArray("effectGroups");
            List<JSONObject> effects = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int level = obj.optInt("requiredSkillLevel", -1);
                if (level > 0) {
                    effects.add(level - 1, obj);
                }
            }
            return effects;
        } catch (JSONException e) {
            throw new RuntimeException("Missing skill effectGroups: " + skillId, e);
        }
    }

    private static List<Object> createEffects(JSONObject effectGroup) {
        List<Object> effectsList = new ArrayList<>();
        if (effectGroup.has("effects")) {
            try {
                JSONArray effectsJson = effectGroup.getJSONArray("effects");
                for (int i = 0; i < effectsJson.length(); i++) {
                    JSONObject effectObj = effectsJson.getJSONObject(i);
                    if (effectObj.has("script")) {
                        String classString = effectObj.getString("script");
                        Object effectInst = Global.getSettings().getScriptClassLoader().loadClass(classString).newInstance();
                        effectsList.add(effectInst);
                    }
                }
            } catch (JSONException e) {
                return new ArrayList<>();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return effectsList;
    }
}
