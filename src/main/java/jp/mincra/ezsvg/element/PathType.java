package jp.mincra.ezsvg.element;

import java.util.Arrays;
import java.util.List;

public enum PathType {
    M, L, Z;

    public static final List<String> TYPES = Arrays.asList("M", "L", "Z");
}