package jp.mincra.ezsvg.elements;

public record PathElement(PathType pathType, float x, float y) {
    @Override
    public String toString() {
        return "{"
                + "\"pathType\":\"" + pathType + "\""
                + ", \"x\":\"" + x + "\""
                + ", \"y\":\"" + y + "\""
                + "}";
    }
}