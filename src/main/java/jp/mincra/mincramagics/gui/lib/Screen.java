package jp.mincra.mincramagics.gui.lib;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @param title GUIのタイトル.
 * @param components GUIのコンポーネントのリスト.
 * @param isModifiableSlot プレイヤーが操作可能なスロットを判定する関数.
 */
@Builder
public record Screen(
        String title,
        List<Component> components,
        Predicate<Integer> isModifiableSlot,
        int size
) {
    public boolean shouldReopen(@Nullable Screen other) {
        if (other == null) return true;
        return size != other.size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Screen other)) return false;
        return title.equals(other.title) && size == other.size && components.size() == other.components.size();
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + components.size();
        result = 31 * result + size;
        return result;
    }
}
