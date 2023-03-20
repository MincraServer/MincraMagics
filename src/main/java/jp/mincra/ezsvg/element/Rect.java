package jp.mincra.ezsvg.element;

import jp.mincra.ezsvg.attribute.Transform;

import java.awt.*;

public class Rect extends SvgElement {
    private final float width;
    private final float height;
    private final float x;
    private final float y;
    private final Transform transform;

    public Rect(Color strokeColor, float width, float height, float x, float y, Transform transform) {
        super(strokeColor);
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.transform = transform;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public Transform transform() {
        return transform;
    }

    @Override
    public String toString() {
        return "{"
                + "\"width\":\"" + width + "\""
                + ", \"height\":\"" + height + "\""
                + ", \"x\":\"" + x + "\""
                + ", \"y\":\"" + y + "\""
                + ", \"transform\":" + transform
                + "}";
    }
}
