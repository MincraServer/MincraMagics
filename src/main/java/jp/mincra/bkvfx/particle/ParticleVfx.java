package jp.mincra.bkvfx.particle;

import jp.mincra.bkvfx.Vfx;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * 直線・円(部分的な円)を描くメソッド
 *
 */
public abstract class ParticleVfx implements Vfx {
    protected final List<Particle> particles = new ArrayList<>();

    /**
     * 直線を描画する
     * @param start 始点の座標
     * @param end 終点の座標
     * @param density 単位長(1)あたり何個のパーティクルを描画するか
     */
    protected void line(Vector start, Vector end, double density) {
        double distance = end.distance(start);
        int amount = (int) (distance * density);
        Vector diff = end.subtract(start);

        for (int i = 0; i < amount; i++) {
            // P = tA, t = i / amount
            Vector p = start.add(diff.multiply(i / amount));
            Particle particle = new Particle();
            particle.setLocation(p);
            particles.add(particle);
        }
    }

    /**
     * 円を描画する
     * @param center 中心
     * @param radius 半径
     * @param density 単位長(1)あたり何個のパーティクルを描画するか
     */
    protected void circle(Vector center, double radius, double density) {
        // 円周 = circumference
        double circumference = 2 * 3.14 * radius;
        int amount = (int) (circumference * density);
        double radianPerI = 2 * 3.14 / amount;

        for (int i = 0; i < amount; i++) {
            double x = Math.cos(i * radianPerI);
            double y = Math.sin(i * radianPerI);
            Vector p = center.add(new Vector(x, y, center.getZ()));
            Particle particle = new Particle();
            particle.setLocation(p);
            particles.add(particle);
        }
    }
}