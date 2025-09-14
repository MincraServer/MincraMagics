package jp.mincra.mincramagics.gui.lib;

import java.util.stream.IntStream;

public record Position(int left, int top, int width, int height) {
    public Position(int left, int top) {
        this(left, top, 1, 1); // Default width and height to 1 if not specified
    }

    public Position(int left, int top, int width) {
        this(left, top, width, 1); // Default height to 1 if not specified
    }

    public int startIndex() {
        return left + top * 9; // Assuming a 9-column inventory layout
    }

    public int endIndex() {
        return startIndex() + width - 1 + (height - 1) * 9;
    }

    /**
     * Position が指定するインデックスのストリームを返す
     * 例:
     * - Position(0, 0, 9, 3) -> 0, 1, 2, ..., 26
     * - Position(0, 0, 3, 2) -> 0, 1, 2, 9, 10, 11
     * - Position(1, 1, 3, 2) -> 10, 11, 12, 19, 20, 21
     * @return インデックスのストリーム
     */
    public IntStream toIndexStream() {
        return IntStream.range(0, height)
                .flatMap(row -> IntStream.range(0, width)
                        .map(col -> (top + row) * 9 + (left + col)));
    }

    /**
     * Position が指定する範囲に index が含まれるか
     * 例:
     * - Position(0, 0, 9, 3): 0, 1, 2, ..., 26 -> true
     * - Position(0, 0, 3, 2): 0, 1, 2, 9, 10, 11 -> true
     * - Position(1, 1, 3, 2): 10, 11, 12, 19, 20, 21 -> true
     * @param index 調べるインデックス
     * @return 範囲に含まれる場合 true
     */
    public boolean isInRange(int index) {
        return toIndexStream().anyMatch(i -> i == index);
    }

    public boolean isNotInRange(int index) {
        return !isInRange(index);
    }
}
