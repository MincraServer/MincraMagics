import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.elements.SvgObject;
import org.bukkit.Particle;

import java.util.regex.Pattern;

public class UnitTest {
    public static void main(String[] args) {
        SvgTest svgTest = new SvgTest();

//        expect("Test 1", isFloatNumeric("12.34"), true);
//        expect("Test 2", isFloatNumeric("12"), false);
//        expect("Test 3", isFloatCoordinates("12.34,56.78"), true);
//        expect("Test 4", isFloatCoordinates("12.34"), false);

        SvgObject svgObject = svgTest.getSvgObject("src/test/resources/magic_circle_1.svg");
        SvgParticleVfx particleVfx = new SvgParticleVfx(svgObject, 8, Particle.FLAME);
        System.out.println(particleVfx);
    }

    private static void expect(String id , Object attempt, Object result) {
        System.out.println(id + ": " + (attempt.equals(result) ? "Success" : "Failure"));
    }

    private static boolean isFloatNumeric(String string) {
        String regex = "^[0-9]+[.][0-9]+$";
        return Pattern.compile(regex).matcher(string).matches();
    }

    private static boolean isFloatCoordinates(String string) {
        String regex = "^[0-9]+[.][0-9]+,[0-9]+[.][0-9]+$";
        return Pattern.compile(regex).matcher(string).matches();
    }
}
