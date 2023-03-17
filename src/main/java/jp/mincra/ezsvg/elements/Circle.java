package jp.mincra.ezsvg.elements;

import java.awt.*;

public class Circle extends SvgElement {
    private final float centerX;
    private final float centerY;
    private final float radius;

    public Circle(Color strokeColor, float centerX, float centerY, float radius) {
        super(strokeColor);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "{"
                + "\"centerX\":\"" + centerX + "\""
                + ", \"centerY\":\"" + centerY + "\""
                + ", \"radius\":\"" + radius + "\""
                + "}";
    }
}
