package jp.mincra.mincramagics.player;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MincraAttributeInstance {
    private static final Map<NamespacedKey, AttributeModifier> modifiers = new HashMap<>();

    public Collection<AttributeModifier> getModifiers() {
        return modifiers.values();
    }

    public AttributeModifier getModifier(NamespacedKey key) {
        return modifiers.get(key);
    }

    public void addModifier(AttributeModifier modifier) {
        modifiers.put(modifier.getKey(), modifier);

    }

    public void removeModifier(NamespacedKey key) {
        modifiers.remove(key);
    }
}
