package jp.mincra.bkvfx.particle;

import jp.mincra.ezsvg.elements.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.List;

public class SvgParticleVfx extends ParticleVfx {
    private final org.bukkit.Particle particleEffect;
    private int amount = 1;
    private float speed ;
    private Vector offset;
    private Color color;
//    private ParticleData particleData;

    /**
     *
     * @param svgObject 描画するパーティクルのSvgObject
     */
    //TODO: パーティクルの中心を(0,0)に設定する
    public SvgParticleVfx(SvgObject svgObject, double density, org.bukkit.Particle particleEffect) {
        this.particleEffect = particleEffect;
        List<SvgElement> svgElements = svgObject.getSvgElements();
        // heightとwidth, 大きい方をsizeにする
        float size = svgObject.getHeight();
        float width = svgObject.getWidth();
        if (width > size) size = width;
        double _size = 1 / size;

        for (SvgElement svgElement : svgElements) {
            if (svgElement instanceof Circle circle) {
                Vector center = new Vector(circle.getCenterX() * _size, 0, circle.getCenterY() * _size);
                double radius = circle.getRadius() * _size;
                circle(center, radius, density);
            } else if (svgElement instanceof Path path) {
                List<PathElement> pathElements = path.getPaths();
                float startX = 0;
                float startY = 0;
                float beforeX = 0;
                float beforeY = 0;
                for (PathElement pathElement : pathElements) {
                    switch (pathElement.pathType()) {
                        case M:
                            beforeX = (float) (pathElement.x() * _size);
                            beforeY = (float) (pathElement.y() * _size);
                            startX = beforeX;
                            startY = beforeY;
                            break;
                        case L:
                            float currentX = (float) (pathElement.x() * _size);
                            float currentY = (float) (pathElement.y() * _size);
                            // 座標系が違う(Yが高さ)
                            Vector start = new Vector(beforeX, 0, beforeY);
                            Vector end = new Vector(currentX, 0, currentY);
                            line(start, end, density);
                            beforeX = currentX;
                            beforeY = currentY;
                            break;
                        case Z:
                            line(new Vector(beforeX, 0, beforeY), new Vector(startX, 0, startY), density);
                            break;
                    }
                }
            }
        }

        offset = new Vector();
    }

    public SvgParticleVfx setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SvgParticleVfx setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public SvgParticleVfx setOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    public SvgParticleVfx setColor(Color color) {
        this.color = color;
        return this;
    }

//    public SvgParticleVfx setParticleData(ParticleData particleData) {
//        this.particleData = particleData;
//        return this;
//    }

    @Override
    public void playEffect(Location center, double scale) {
        System.out.println("Play effect in SvgParticleVfx");
        for (Particle particle : particles) {
            Vector offset = particle.getOffset();
            if (offset == null) offset = this.offset;
            int amount = particle.getAmount();
            if (amount == 0) amount = this.amount;
            float speed = particle.getSpeed();
            if (speed == 0) speed = this.speed;
            Color color = particle.getColor();
            if (color == null) color = this.color;
//            ParticleData particleData = particle.getParticleData();
//            if (particleData == null) particleData = this.particleData;

            Location _center = center.clone();
            Vector pLoc = particle.getLocation().clone();
            center.getWorld().spawnParticle(particleEffect,
                    _center.add(pLoc.multiply(scale)),
                    amount,
                    offset.getX(),
                    offset.getY(),
                    offset.getZ(),
                    speed);
        }
    }

    @Override
    public String toString() {
        return "{"
                + "\"particleEffect\":\"" + particleEffect + "\""
                + ", \"amount\":\"" + amount + "\""
                + ", \"speed\":\"" + speed + "\""
                + ", \"particles\":" + particles
                + "}";
    }
}
