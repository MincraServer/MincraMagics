package jp.mincra.bkvfx;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface Vfx {
    String LAVA_HEXAGON = "lava_hexagon";
    String WAX_ON_PENTAGON = "wax_on_pentagon";
    String WAX_OFF_PENTAGON = "wax_off_pentagon";

    /**
     *
     * @param loc 描画するパーティクルの中心の座標
     * @param scale 描画するパーティクルの大きさを1ブロック=1dとして指定する
     */
    void playEffect(Location loc, double scale);

    /**
     *
     * @param loc 描画するパーティクルの中心の座標
     * @param scale 描画するパーティクルの大きさを1ブロック=1dとして指定する
     * @param axis 回転軸
     * @param angle 回転角度
     */
    void playEffect(Location loc, double scale, Vector axis, double angle);
}
