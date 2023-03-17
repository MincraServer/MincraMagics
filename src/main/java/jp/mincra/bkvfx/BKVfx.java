package jp.mincra.bkvfx;

import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import org.bukkit.Particle;

public class BKVfx {
    private static BKVfx instance;
    public static BKVfx instance() {
        if (instance == null) instance = new BKVfx();
        return instance;
    }

    private final VfxManager vfxManager;

    public BKVfx() {
        System.out.println("[BKVfx] BKVfx()");
        vfxManager = new VfxManager();
        registerNativeVfx();
    }

    private void registerNativeVfx() {
        String magic_circle_1 = """
                <svg
                                   width="26.458334mm"
                                   height="26.458334mm">
                                  <circle
                                          style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          cx="13.229167"
                                          cy="13.229167"
                                          r="13.164189" />
                                  <circle
                                          style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.10836;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          cx="13.229167"
                                          cy="13.229167"
                                          r="10.976695" />
                                  <path
                                          style="fill:none;stroke:#000000;stroke-width:0.128678;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          d="M 13.233958,2.5083099 L 6.9184415,21.902415 L 23.427371,9.9244218 L 3.0308783,9.9111168 L 19.524168,21.910637 Z"
                                  />
                                </svg>""";
        String just_circle = """
                <svg
                                   width="26.458334mm"
                                   height="26.458334mm">
                                  <circle
                                          style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          cx="13.229167"
                                          cy="13.229167"
                                          r="13.164189" />
                                </svg>""";
        String dual_circle = """
                <svg
                                   width="26.458334mm"
                                   height="26.458334mm">
                                  <circle
                                          style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          cx="13.229167"
                                          cy="13.229167"
                                          r="13.164189" />
                                  <circle
                                          style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                                          cx="13.229167"
                                          cy="13.229167"
                                          r="10" />
                                </svg>""";
        vfxManager.registerVfx("inferno",
                new SvgParticleVfx(SvgFactory.fromString(magic_circle_1),
                        15, Particle.FLAME));
        vfxManager.registerVfx("circle",
                new SvgParticleVfx(SvgFactory.fromString(just_circle),
                        15, Particle.SOUL_FIRE_FLAME).setAmount(3));
        vfxManager.registerVfx("dual_circle",
                new SvgParticleVfx(SvgFactory.fromString(dual_circle),
                        15, Particle.SOUL_FIRE_FLAME).setAmount(3));

        System.out.println("[BKVfx] native vfx registered.");
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }
}
