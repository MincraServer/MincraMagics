package jp.mincra.bkvfx;

import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class BKVfx {
    private static BKVfx instance;
    public static BKVfx instance() {
        if (instance == null) instance = new BKVfx();
        return instance;
    }

    private final VfxManager vfxManager;

    public BKVfx() {
        vfxManager = new VfxManager();
        registerNativeVfx();
    }

    private void registerNativeVfx() {
        String five_pointed_star_dual_circle = """
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
        String six_pointed_star_dual_circle = """
               <svg
                                   width="26.458334mm"
                                   height="26.458334mm">
                      <path
                         style="fill:none;stroke:#000000;stroke-width:0.1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                         d="M 13.149329,2.1960553 3.723959,18.960853 22.955385,18.741064 13.149329,2.1960553"
                         id="path1132" />
                      <path
                         style="fill:none;stroke:#000000;stroke-width:0.1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                         d="M 3.5970638,7.8575844 22.82849,7.6377952 13.40312,24.402593 3.5970638,7.8575844"
                         id="path1134" />
                    <circle
                       style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.13229167;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                       id="path950"
                       cx="13.229167"
                       cy="13.229167"
                       r="13.401068" />
                    <circle
                       style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.105423;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                       id="ellipse1170"
                       cx="13.229167"
                       cy="13.229167"
                       r="10.679449" />
                </svg>
                               
                """;
        vfxManager.registerVfx("inferno",
                new SvgParticleVfx(SvgFactory.fromString(five_pointed_star_dual_circle),
                        5, Particle.FLAME).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("soul",
                new SvgParticleVfx(SvgFactory.fromString(six_pointed_star_dual_circle),
                        5, Particle.SOUL_FIRE_FLAME).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }


}
