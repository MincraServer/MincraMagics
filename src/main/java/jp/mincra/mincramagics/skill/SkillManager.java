package jp.mincra.mincramagics.skill;

import java.util.HashMap;
import java.util.Map;

public class SkillManager {
    private final Map<String, MagicSkill> idToSkill = new HashMap<>();

    public void registerSkill(String id, MagicSkill skill) {
        idToSkill.put(id, skill);
    }

    public MagicSkill getSkill(String id) {
        return idToSkill.get(id);
    }

    public boolean isRegistered(String id) {
        return idToSkill.containsKey(id);
    }
}
