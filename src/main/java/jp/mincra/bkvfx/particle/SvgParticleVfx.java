package jp.mincra.bkvfx.particle;

import jp.mincra.ezsvg.elements.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SvgParticleVfx extends ParticleVfx {
    private final org.bukkit.Particle particleEffect;
    private int amount = 1;
    private float speed ;
    private Vector offset;
    private Color color;
//    private ParticleData particleData;

    private final double density;
    private final List<CircleProperty> circles;
    private final List<LineProperty> lines;

    /**
     *
     * @param svgObject 描画するパーティクルのSvgObject
     */
    //TODO: パーティクルの中心を(0,0)に設定する
    public SvgParticleVfx(SvgObject svgObject, double density, org.bukkit.Particle particleEffect) {
        this.density = density;
        this.particleEffect = particleEffect;
        circles = new ArrayList<>();
        lines = new ArrayList<>();
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
                circles.add(new CircleProperty(center, radius));
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
                            lines.add(new LineProperty(start, end));
                            beforeX = currentX;
                            beforeY = currentY;
                        }
                        case Z -> lines.add(new LineProperty(
                                new Vector(beforeX, 0, beforeY), new Vector(startX, 0, startY)
                        ));
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
    public void playEffect(Location loc, double scale) {
        for (CircleProperty circle : circles) {
            circle(circle.center().clone().multiply(scale), circle.radius() * scale, density);
        }

        for (LineProperty line : lines) {
            line(line.start().clone().multiply(scale), line.end().clone().multiply(scale), density);
        }

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

            Location _loc = loc.clone();
            Vector pLoc = particle.getLocation().clone();
            loc.getWorld().spawnParticle(particleEffect,
                    _loc.add(pLoc),
                    amount,
                    offset.getX(),
                    offset.getY(),
                    offset.getZ(),
                    speed);
        }

        particles = new ArrayList<>();
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

record CircleProperty(Vector center, double radius) {}
record LineProperty(Vector start, Vector end) {}
