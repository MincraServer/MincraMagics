package jp.mincra.ezsvg.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SvgObject {
    private final List<SvgElement> svgElements;
    private final float width;
    private final float height;

    /**
     * @param width Width as mm
     * @param height Height as mm
     */
    public SvgObject(float width, float height) {
        this.svgElements = new ArrayList<>();
        this.width = width;
        this.height = height;
    }

    public void addSvgElement(SvgElement svgElement) {
        svgElements.add(svgElement);
    }

    public List<SvgElement> getSvgElements() {
        return Collections.unmodifiableList(svgElements);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "{"
                + "\"svgElements\":" + svgElements
                + ", \"width\":\"" + width + "\""
                + ", \"height\":\"" + height + "\""
                + "}";
    }
}
