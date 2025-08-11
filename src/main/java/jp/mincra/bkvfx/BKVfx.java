package jp.mincra.bkvfx;

import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.Map;

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
        var svgs = svgs();

        vfxManager.registerVfx("inferno",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("five_pointed_star_dual_circle")),
                        5, Particle.FLAME).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("soul",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.SOUL_FIRE_FLAME).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("complex",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("complex_five_pointed_circle")),
                        5, Particle.HAPPY_VILLAGER).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("rect",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("rect")),
                        5, Particle.FLAME));
        vfxManager.registerVfx("charging",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("five_pointed_star_dual_circle")),
                        5, Particle.ENCHANT).setSpeed(0.003f).setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("move",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.FIREWORK).setSpeed(0.1f));
        vfxManager.registerVfx("jump",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.CLOUD).setSpeed(0.1f));
        vfxManager.registerVfx("wraith",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("five_pointed_star_dual_circle")),
                        6, Particle.LARGE_SMOKE).setSpeed(0.1f));
        vfxManager.registerVfx("scorch",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("five_pointed_star_dual_circle")),
                        6, Particle.LARGE_SMOKE).setSpeed(0.1f));
        vfxManager.registerVfx("snowbomb",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        6, Particle.ITEM_SNOWBALL).setSpeed(0.1f));
        vfxManager.registerVfx("ice",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.DUST_COLOR_TRANSITION)
                        .setSpeed(0.1f)
                        .setDustTransition(
                                new Particle.DustTransition(
                                        Color.fromRGB(164, 236, 254),
                                        Color.fromRGB(173, 209, 255),
                                        1
                                )
                        ));
        vfxManager.registerVfx("healing",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.HAPPY_VILLAGER)
                        .setSpeed(0.1f));
        vfxManager.registerVfx("instant_effect_pentagon",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("five_pointed_star_dual_circle")),
                        5, Particle.INSTANT_EFFECT)
                        .setSpeed(0.1f)
                        .setOffset(new Vector(0.003, 0.003, 0.003)));
        vfxManager.registerVfx("happy_villager_hexagon",
                new SvgParticleVfx(SvgFactory.fromString(svgs.get("six_pointed_star_dual_circle")),
                        5, Particle.HAPPY_VILLAGER)
                        .setSpeed(0.1f)
                        .setOffset(new Vector(0.003, 0.003, 0.003)));
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }

    public Map<String, String> svgs() {
        return Map.of(
                "five_pointed_star_dual_circle", """
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
                                        </svg>""",
                "six_pointed_star_dual_circle", """
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
                         """,
                "complex_five_pointed_circle", """
                        <svg
                           width="26.458334mm"
                           height="26.458334mm">
                        <circle
                               style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                               id="path950"
                               cx="13.229167"
                               cy="13.229167"
                               r="13.164189" />
                            <path
                               style="fill:none;stroke:#000000;stroke-width:0.107459;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                               d="M 13.233168,4.2761978 7.9590911,20.472181 21.745669,10.46938 4.7125938,10.458269 18.486111,20.479048 Z"
                               id="path1803" />
                            <circle
                               style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.0904913;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                               id="circle2482"
                               cx="13.229167"
                               cy="13.229167"
                               r="9.1666193" />
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:0.0992428;stroke-miterlimit:4;stroke-dasharray:none"
                               id="rect6269"
                               width="18.47427"
                               height="18.47427"
                               x="-9.237134"
                               y="-27.946001"
                               transform="rotate(135)" />
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:0.0992428;stroke-miterlimit:4;stroke-dasharray:none"
                               id="rect6351"
                               width="18.47427"
                               height="18.47427"
                               x="-18.591568"
                               y="-25.439489"
                               transform="rotate(165)" />
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:0.0992428;stroke-miterlimit:4;stroke-dasharray:none"
                               id="rect6353"
                               width="18.47427"
                               height="18.47427"
                               x="-25.439489"
                               y="-18.591568"
                               transform="rotate(-165)" />
                            <circle
                               style="fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.140025;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1"
                               id="circle6700"
                               cx="13.229167"
                               cy="13.229167"
                               r="14.184415" />
                        </svg>
                        """,
                "rect", """
                        <svg
                           width="100mm"
                           height="100mm">
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:1"
                               id="rect2083"
                               width="47.540157"
                               height="46.700226"
                               x="0.50395924"
                               y="0.83993208" />
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:1"
                               id="rect2085"
                               width="57.451355"
                               height="55.099548"
                               x="31.749434"
                               y="22.174208" />
                            <rect
                               style="fill:none;stroke:#000000;stroke-width:1"
                               id="rect2087"
                               width="43.004524"
                               height="39.812782"
                               x="26.205881"
                               y="55.771488" />
                        </svg>

                                                """
        );
    }
}
