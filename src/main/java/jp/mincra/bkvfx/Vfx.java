package jp.mincra.bkvfx;

import org.bukkit.Location;

public interface Vfx {
    /**
     *
     * @param center 描画するパーティクルの中心の座標
     * @param scale 描画するパーティクルの大きさを1ブロック=1dとして指定する
     */
    void playEffect(Location center, double scale);
}
