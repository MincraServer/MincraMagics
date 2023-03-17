package jp.mincra.ezsvg;

import java.util.regex.Pattern;

public class SvgRegex {
    private Pattern floatCoodinatesPattern = null;

    public boolean isFloatCoordinates(String string) {
        if (floatCoodinatesPattern == null) {
            String regex = "^[0-9]+[.][0-9]+,[0-9]+[.][0-9]+$";
            floatCoodinatesPattern = Pattern.compile(regex);
        }
        return floatCoodinatesPattern.matcher(string).matches();
    }
}
