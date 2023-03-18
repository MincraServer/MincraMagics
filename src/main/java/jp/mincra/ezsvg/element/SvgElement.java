package jp.mincra.ezsvg.element;

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
