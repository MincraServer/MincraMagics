package jp.mincra.mincramagics.gui.lib;

public record Position(int left, int top, int width, int height) {
    public Position(int left, int top, int width) {
        this(left, top, width, 1); // Default height to 1 if not specified
    }

    public int startIndex() {
        return left + top * 9; // Assuming a 9-column inventory layout
    }

    public int endIndex() {
        return startIndex() + width - 1; // Calculate the end index based on width
    }
}
