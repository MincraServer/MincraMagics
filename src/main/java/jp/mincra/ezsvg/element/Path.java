package jp.mincra.ezsvg.element;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Only support for "M, L, Z"
 */
public class Path extends SvgElement {
    private final List<PathElement> paths;

    public Path(Color strokeColor, List<PathElement> paths) {
        super(strokeColor);
        this.paths = paths;
    }

    public List<PathElement> getPaths() {
        return Collections.unmodifiableList(paths);
    }

    @Override
    public String toString() {
        return "{"
                + "\"paths\":" + paths
                + "}";
    }
}