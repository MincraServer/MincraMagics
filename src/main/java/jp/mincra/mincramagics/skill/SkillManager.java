package jp.mincra.mincramagics.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkillManager {
    private final Map<String, MagicSkill> idToSkill = new HashMap<>();

    /**
     *
     * @param skillId スキルのID. 原則スキルクラスをlower caseにしたもの.
     * @param skill MagicSkillの具象クラス
     */
    public void registerSkill(String skillId, MagicSkill skill) {
        idToSkill.put(skillId, skill);
    }

    public MagicSkill getSkill(String id) {
        return idToSkill.get(id);
    }

    public boolean isRegistered(String id) {
        return idToSkill.containsKey(id);
    }

    public Set<String> getSkillIds() {
        return idToSkill.keySet();
    }
}
