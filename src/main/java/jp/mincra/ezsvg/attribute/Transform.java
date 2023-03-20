package jp.mincra.ezsvg.attribute;

import java.util.Map;

public class Transform {
    // -180° ~ 180°
    private float rotate = 0;

    public Transform() {}

    public Transform(Map<String, String> keyValue) {
        if (keyValue.containsKey("rotate")) this.rotate = Float.parseFloat(keyValue.get("rotate"));
    }

    public float rotate() {
        return rotate;
    }

    @Override
    public String toString() {
        return "{"
                + "\"rotate\":\"" + rotate + "\""
                + "}";
    }
}
