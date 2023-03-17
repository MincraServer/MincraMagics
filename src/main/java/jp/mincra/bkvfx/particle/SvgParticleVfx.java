package jp.mincra.bkvfx.particle;

import jp.mincra.ezsvg.elements.*;
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
        double invSize = 1 / size;
        double halfSize = size / 2;

        for (SvgElement svgElement : svgElements) {
            if (svgElement instanceof Circle circle) {
                Vector center = new Vector((circle.getCenterX() - halfSize) * invSize, 0, (circle.getCenterY() - halfSize) * invSize);
                double radius = circle.getRadius() * invSize;
                circle(center, radius, density);
            } else if (svgElement instanceof Path path) {
                List<PathElement> pathElements = path.getPaths();
                float startX = 0;
                float startY = 0;
                float beforeX = 0;
                float beforeY = 0;
                for (PathElement pathElement : pathElements) {
                    switch (pathElement.pathType()) {
                        case M -> {
                            beforeX = (float) ((pathElement.x() - halfSize) * invSize);
                            beforeY = (float) ((pathElement.y() - halfSize) * invSize);
                            startX = beforeX;
                            startY = beforeY;
                        }
                        case L -> {
                            float currentX = (float) ((pathElement.x() - halfSize) * invSize);
                            float currentY = (float) ((pathElement.y() - halfSize) * invSize);
                            // 座標系が違う(Yが高さ)
                            Vector start = new Vector(beforeX, 0, beforeY);
                            Vector end = new Vector(currentX, 0, currentY);
                            line(start, end, density);
                            beforeX = currentX;
                            beforeY = currentY;
                        }
                        case Z -> line(new Vector(beforeX, 0, beforeY), new Vector(startX, 0, startY), density);
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
