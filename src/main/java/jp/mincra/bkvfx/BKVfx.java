package jp.mincra.bkvfx;

import jp.mincra.bkvfx.particle.SvgParticleVfx;
import jp.mincra.ezsvg.SvgFactory;
import xyz.xenondevs.particle.ParticleEffect;

public class BKVfx {
    private static final BKVfx instance = new BKVfx();
    public static BKVfx instance() {
        return instance;
    }

    private final VfxManager vfxManager;

    public BKVfx() {
        vfxManager = new VfxManager();
        registerNativeVfx();
    }

    private void registerNativeVfx() {
        vfxManager.registerVfx("inferno",
                new SvgParticleVfx(SvgFactory.fromXMLPath("src/main/resources/magic_circle_1.svg"),
                        8, ParticleEffect.FLAME));

    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }
}
