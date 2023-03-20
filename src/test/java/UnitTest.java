import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import org.bukkit.Particle;

public class UnitTest {
    public static void main(String[] args) {
//        SvgTest svgTest = new SvgTest();
//        SvgObject svgObject = svgTest.getSvgObject("src/test/resources/five_pointed_star_dual_circle.svg");
//        SvgParticleVfx particleVfx = new SvgParticleVfx(svgObject, 8, Particle.FLAME);
//        System.out.println(particleVfx);

//        expect("Quaternion Test 1",
//                new Quaternion(new Vector(0, 1, 0), Math.PI / 6)
//                        .rotate(new Vector(1, 0, 0)),
//                new Vector(Math.cos(Math.PI / 6), 0, Math.sin(Math.PI / 6)));
//        expect("Quaternion Test 2",
//                new Quaternion(new Vector(1, 0, 0), Math.PI / 6)
//                        .rotate(new Vector(1, 0, 0)),
//                new Vector(1, 0, 0));

//        expect("EZSvg:SvgParser.#parseTransform() Test",
//                new SvgParser().parseTransform("rotate(-165)"),
//                Map.of("rotate", "-165"));
        expect("SvgParticleVfx Test",
                new SvgParticleVfx(SvgFactory.fromString("""

                        """), 5, Particle.FLAME),
                null);
    }

    private static void expect(String id , Object attempt, Object result) {
        System.out.println(id + ": " + (attempt.equals(result) ? "Success" : "Failure"));
        if (attempt.equals(result)) {
            System.out.println(id + ": " + "Success");
        } else {
            System.out.println(id + ": " + "Failure - Attempt: " + attempt);
        }
    }
}
