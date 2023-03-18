package jp.mincra.ezsvg.element;

public class PathElement {
    private final PathType pathType;
    private final float x;
    private final float y;

    public PathElement(PathType pathType, float x, float y) {
        this.pathType = pathType;
        this.x = x;
        this.y = y;
    }

    public PathType pathType() {
        return pathType;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    @Override
    public String toString() {
        return "{"
                + "\"pathType\":\"" + pathType + "\""
                + ", \"x\":\"" + x + "\""
                + ", \"y\":\"" + y + "\""
                + "}";
    }
}