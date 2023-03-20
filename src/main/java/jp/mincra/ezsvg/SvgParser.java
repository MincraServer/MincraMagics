package jp.mincra.ezsvg;

import jp.mincra.ezsvg.element.PathElement;
import jp.mincra.ezsvg.element.PathType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SvgParser {
    public SvgRegex regex;
    
    public float parseSizeAsMM(String size) {
        if (size.endsWith("mm")) {
            // In case mm
            return Float.parseFloat(size.replaceAll("mm", ""));
        } else {
            // In case px
            return Float.parseFloat(size) * 0.26458334f;
        }
    }

    public Map<String, String> parseStyle(String styleStr) {
        String[] styles = styleStr.split(";");
        Map<String, String> styleMap = new HashMap<>();
        for (String style : styles) {
            String[] keyValue = style.split(":");
            styleMap.put(keyValue[0], keyValue[1]);
        }
        return styleMap;
    }

    public List<PathElement> parsePathD(String dStr) {
        String[] dArray = dStr.split(" ");
        String tmpCommand = null;
        List<PathElement> pathElements = new ArrayList<>();
        
        if (regex == null) regex = new SvgRegex();

        for (String d : dArray) {
            if (regex.isFloatCoordinates(d)) {
                if (tmpCommand == null) {
                    tmpCommand = "L";
                }
                String[] xy = d.split(",");

                if (!PathType.TYPES.contains(tmpCommand)) {
                    System.out.println("Only " + PathType.TYPES + " are available.");
                    break;
                }

                pathElements.add(new PathElement(PathType.valueOf(tmpCommand),
                        Float.parseFloat(xy[0]), Float.parseFloat(xy[1])));
                tmpCommand = null;
            } else {
                tmpCommand = d;

                if (tmpCommand.equals("Z")) {
                    pathElements.add(new PathElement(PathType.valueOf(tmpCommand), 0, 0));
                }
            }
        }
        return pathElements;
    }

    public Map<String, String> parseTransform(String strTrans) {
        Map<String, String> mapTrans = new HashMap<>();

        String[] splitStr = strTrans.split(" ");

        for (String str : splitStr) {
            String key = str.substring(0, str.indexOf('('));
            String value = str.substring(str.indexOf('(') + 1, str.indexOf(')'));
//            System.out.println("[EZSvg:SvgParser.#parseTransform()] k: " + key + ", v: " + value);
            mapTrans.put(key, value);
        }

        return mapTrans;
    }
}
