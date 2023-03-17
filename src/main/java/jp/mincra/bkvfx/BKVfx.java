package jp.mincra.bkvfx;

import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import org.bukkit.Bukkit;
import xyz.xenondevs.particle.ParticleEffect;

public class BKVfx {
    private static BKVfx instance;
    public static BKVfx instance() {
        instance = new BKVfx();
        return instance;
    }

    private final VfxManager vfxManager;

    public BKVfx() {
        Bukkit.getLogger().info("[BKVfx] BKVfx()");
        vfxManager = new VfxManager();
        registerNativeVfx();
    }

    private void registerNativeVfx() {
        String magic_circle_1 = "<svg\n" +
                "                   width=\"26.458334mm\"\n" +
                "                   height=\"26.458334mm\">\n" +
                "                  <circle\n" +
                "                          style=\"fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.129954;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1\"\n" +
                "                          cx=\"13.229167\"\n" +
                "                          cy=\"13.229167\"\n" +
                "                          r=\"13.164189\" />\n" +
                "                  <path\n" +
                "                          style=\"fill:none;stroke:#000000;stroke-width:0.128678;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1\"\n" +
                "                          d=\"M 13.233958,2.5083099 L 6.9184415,21.902415 L 23.427371,9.9244218 L 3.0308783,9.9111168 L 19.524168,21.910637 Z\"\n" +
                "                  />\n" +
                "                  <circle\n" +
                "                          style=\"fill:none;fill-opacity:1;stroke:#000000;stroke-width:0.10836;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1\"\n" +
                "                          cx=\"13.229167\"\n" +
                "                          cy=\"13.229167\"\n" +
                "                          r=\"10.976695\" />\n" +
                "                </svg>";
        vfxManager.registerVfx("inferno",
                new SvgParticleVfx(SvgFactory.fromString(magic_circle_1),
                        8, ParticleEffect.FLAME));

        Bukkit.getLogger().info("[BKVfx] native vfx registered.");
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }
}
