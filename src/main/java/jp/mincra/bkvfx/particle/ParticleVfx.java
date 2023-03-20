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
    protected List<Particle> particles = new ArrayList<>();

    /**
     * 直線を描画する
     * @param start 始点の座標
     * @param end 終点の座標
     * @param density 単位長(1)あたり何個のパーティクルを描画するか
     */
    protected void line(Vector start, Vector end, double density) {
        double distance = end.distance(start);
        int amount = (int) (distance * density);
        Vector diff = end.clone();
        diff = diff.subtract(start);

        for (int i = 0; i < amount; i++) {
            // P = tA, t = i / amount
            Vector _diff = diff.clone();
            Vector _start = start.clone();
            Vector p = _start.add(_diff.multiply((float) i / (float) amount));
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
        double radianPerI = 2 * 3.14f / (float) amount;

        for (int i = 0; i < amount; i++) {
            double x = Math.cos(i * radianPerI) * radius;
            double z = Math.sin(i * radianPerI) * radius;
            Vector _center = center.clone();
            Vector p = _center.add(new Vector(x, 0, z));
            Particle particle = new Particle();
            particle.setLocation(p);
            particles.add(particle);
        }
    }

    /**
     * 四角形を描く
     * @param upperLeft
     *      * ↓ Here
     *      *  ______
     *      *  \     \
     *      *   \     \
     *      *    \     \
     *      *     -------
     * @param width width of rectangle
     * @param height height of rectangle
     * @param rotate angle of rectangle as degree (-180 ~ 180)
     */
    protected void rect(Vector upperLeft, double width, double height, double rotate, Vector origin,
                        double density) {
        Vector _upperLeft = upperLeft.clone();
        Vector upperRight = upperLeft.clone().add(new Vector(width, 0, 0));
        Vector lowerRight = upperRight.clone().add(new Vector(width, 0, height));
        Vector lowerLeft = upperLeft.clone().add(new Vector(0, 0, height));

        // Rotate
        double radian = Math.toRadians(rotate);
        Vector axis = new Vector(0, 1, 0);
        _upperLeft = rotate(_upperLeft, origin, axis, radian);
        upperRight = rotate(upperRight, origin, axis, radian);
        lowerLeft = rotate(lowerLeft, origin, axis, radian);
        lowerRight = rotate(lowerRight, origin, axis, radian);

        line(_upperLeft, upperRight, density);
        line(upperRight, lowerRight, density);
        line(lowerRight, lowerLeft, density);
        line(lowerLeft, _upperLeft, density);
    }

    private Vector rotate(Vector vec, Vector origin, Vector axis, double radian) {
        return vec.subtract(origin).rotateAroundAxis(axis, radian).add(origin);
    }
}