import jp.mincra.bkvfx.Quaternion;
import org.bukkit.util.Vector;

public class UnitTest {
    public static void main(String[] args) {
//        SvgTest svgTest = new SvgTest();
//        SvgObject svgObject = svgTest.getSvgObject("src/test/resources/magic_circle_1.svg");
//        SvgParticleVfx particleVfx = new SvgParticleVfx(svgObject, 8, Particle.FLAME);
//        System.out.println(particleVfx);

        expect("Quaternion Test 1",
                new Quaternion(new Vector(0, 1, 0), Math.PI / 6)
                        .rotate(new Vector(1, 0, 0)),
                new Vector3(Math.cos(Math.PI / 6), 0, Math.sin(Math.PI / 6)));
        expect("Quaternion Test 2",
                new Quaternion(new Vector(1, 0, 0), Math.PI / 6)
                        .rotate(new Vector(1, 0, 0)),
                new Vector3(1, 0, 0));
    }

    private static void expect(String id , Object attempt, Object result) {
        System.out.println(id + ": " + (attempt.equals(result) ? "Success" : "Failure"));
        if (!attempt.equals(result)) {
            System.out.println("Attempt: " + attempt);
        }
    }
}
