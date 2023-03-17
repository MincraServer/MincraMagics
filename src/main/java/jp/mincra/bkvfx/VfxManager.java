package jp.mincra.bkvfx;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class VfxManager {
    private final Map<String, Vfx> vfxMap;

    public VfxManager() {
        this.vfxMap = new HashMap<>();
    }

    public void registerVfx(String vfxId, Vfx bkVfx) {
        vfxMap.put(vfxId, bkVfx);
    }

    public Vfx getVfx(String vfxId) {
        Bukkit.getLogger().info("getVfx() " + vfxId);
        return vfxMap.get(vfxId);
    }
}
