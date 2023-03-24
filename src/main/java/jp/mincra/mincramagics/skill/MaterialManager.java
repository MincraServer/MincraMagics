package jp.mincra.mincramagics.skill;

import java.util.HashMap;
import java.util.Map;

public class MaterialManager {
    private final Map<String, MaterialProperty> idToMaterial = new HashMap<>();
    public void registerMaterial(String materialId, MaterialProperty materialProperty) {
        idToMaterial.put(materialId, materialProperty);
    }

    public MaterialProperty getMaterial(String materialId) {
        return idToMaterial.get(materialId);
    }

    public boolean isRegistered(String materialId) {
        return idToMaterial.containsKey(materialId);
    }
}
