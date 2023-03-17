import jp.mincra.bkvfx.Quaternion;
import jp.mincra.bkvfx.Vector3;
import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.elements.SvgObject;
import org.bukkit.Particle;

import java.util.regex.Pattern;

public class UnitTest {
    public static void main(String[] args) {
//        SvgTest svgTest = new SvgTest();
//        SvgObject svgObject = svgTest.getSvgObject("src/test/resources/magic_circle_1.svg");
//        SvgParticleVfx particleVfx = new SvgParticleVfx(svgObject, 8, Particle.FLAME);
//        System.out.println(particleVfx);

        expect("Quaternion Test 1",
                new Quaternion()
                        .set(new Vector3(0, 1, 0), Math.PI / 6)
                        .rotate(new Vector3(1, 0, 0)),
                new Vector3(Math.cos(Math.PI / 6), 0, Math.sin(Math.PI / 6)));
        expect("Quaternion Test 2",
                new Quaternion()
                        .set(new Vector3(1, 0, 0), Math.PI / 6)
                        .rotate(new Vector3(1, 0, 0)),
                new Vector3(1, 0, 0));
    }

    private static void expect(String id , Object attempt, Object result) {
        System.out.println(id + ": " + (attempt.equals(result) ? "Success" : "Failure"));
        if (!attempt.equals(result)) {
            System.out.println("Attempt: " + attempt);
        }
    }
}
