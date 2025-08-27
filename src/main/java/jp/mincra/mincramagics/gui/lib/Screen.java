package jp.mincra.mincramagics.gui.lib;

import lombok.Builder;

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
        Predicate<Integer> isModifiableSlot
) {
}
