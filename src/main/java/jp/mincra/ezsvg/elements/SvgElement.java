package jp.mincra.ezsvg.elements;

import java.awt.*;

public abstract class SvgElement {
    protected final Color strokeColor;

    public SvgElement(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }
}
