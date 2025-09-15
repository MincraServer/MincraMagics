package jp.mincra.mincramagics.hud;

import jp.mincra.mincramagics.MaterialSlot;
import jp.mincra.mincramagics.font.Fonts;
import jp.mincra.mincramagics.nbt.ArtifactNBT;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Map;

public class SkillHudController {
    public Hud draw(ItemStack item) {
        final var artifactNBT = ArtifactNBT.fromItem(item);
        if (artifactNBT == null) return draw(Map.of());

        return draw(artifactNBT.getMaterialMap());
    }

    public Hud draw(Map<MaterialSlot, String> materials) {
        final var shift = Fonts.shift(8) + Fonts.shift(4) + Fonts.shift(2) + Fonts.shift(1);

        if (materials.isEmpty()) return new Hud(shift, Fonts.shift(16));

        return new Hud(
                materials.entrySet().stream()
                        .sorted(Comparator.comparingInt(a -> a.getKey().getOrder()))
                        .map(entry -> Fonts.slot(entry.getKey(), true) + Fonts.shift(1) + Fonts.negShift(12) + Fonts.material(entry.getValue(), true))
                        .reduce("", (a, b) -> a + b)
                        + shift,
                Fonts.shift(1).repeat(materials.size()) + Fonts.shift(4).repeat(materials.size()) + Fonts.shift(8).repeat(materials.size())
                + Fonts.shift(16)
        );
    }
}
