package jp.mincra.bkvfx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VfxManager {
    private final Map<String, Vfx> vfxMap;

    public VfxManager() {
        this.vfxMap = new HashMap<>();
    }

    public void registerVfx(String vfxId, Vfx bkVfx) {
        vfxMap.put(vfxId, bkVfx);
    }

    public Vfx getVfx(String vfxId) {
        System.out.println("getVfx() " + vfxId);
        return vfxMap.get(vfxId);
    }

    public Set<String> getVfxIds() {
        return vfxMap.keySet();
    }
}
