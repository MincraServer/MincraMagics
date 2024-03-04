package jp.mincra.bkvfx.particle;

import jp.mincra.ezsvg.attribute.Transform;
import jp.mincra.ezsvg.element.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SvgParticleVfx extends ParticleVfx {
    private final org.bukkit.Particle particleEffect;
    private int amount = 1;
    private float speed ;
    private Vector offset;
    private Color color;
//    private ParticleData particleData;
    private Vector origin;

    private final double density;
    private final List<CircleProperty> circles;
    private final List<LineProperty> lines;
    private final List<RectProperty> rects;

    /**
     *
     * @param svgObject 描画するパーティクルのSvgObject
     */
    //TODO: パーティクルの中心を(0,0)に設定する
    public SvgParticleVfx(SvgObject svgObject, double density, org.bukkit.Particle particleEffect) {
        // Initialize params
        this.density = density;
        this.particleEffect = particleEffect;
        circles = new ArrayList<>();
        lines = new ArrayList<>();
        rects = new ArrayList<>();

        List<SvgElement> svgElements = svgObject.getSvgElements();
        // heightとwidth, 大きい方をsizeにする
        float height = svgObject.getHeight();
        float width = svgObject.getWidth();
        float size = Math.max(width, height);
        double invSize = 1 / size;
        double halfSize = size / 2;

        this.origin = new Vector(-width / 2, 0, -height / 2);

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
            } else if (svgElement instanceof Rect rect) {
                rects.add(new RectProperty(
                        rect.width() * invSize,
                        rect.height() * invSize,
                        (rect.x() - halfSize) * invSize,
                        (rect.y() - halfSize) * invSize,
                        rect.transform()));
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
    public void playEffect(Location loc, double scale, Vector axis, double angle) {
        for (CircleProperty circle : circles) {
            circle(circle.center().clone().multiply(scale),
                    circle.radius() * scale,
                    density);
        }
        for (LineProperty line : lines) {
            line(line.start().clone().multiply(scale),
                    line.end().clone().multiply(scale),
                    density);
        }
        for (RectProperty rect : rects) {
            Bukkit.getLogger().log(Level.INFO, "Draw rect: " + rect.toString());
            rect(new Vector(rect.x() * scale, 0, rect.y() * scale),
                    rect.width() * scale,
                    rect.height() * scale,
                    rect.transform().rotate(),
                    origin,
                    density);
        }


        for (ParticleData particle : particles) {
            // Set default parameters
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

            // Rotate
            Location _loc = loc.clone();
            Vector pLoc = particle.getLocation().clone();
            pLoc = pLoc.rotateAroundAxis(axis, Math.toRadians(angle));

            // Spawn Particle
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
    public void playEffect(Location loc, double scale) {
        playEffect(loc, scale, new Vector(0, 0, 0), 0);
    }

    @Override
    public String toString() {
        return "{"
                + "\"circles\":" + circles
                + ", \"lines\":" + lines
                + ", \"rects\":" + rects
                + "}";
    }
}

record CircleProperty(Vector center, double radius) {
    @Override
    public String toString() {
        return "{"
                + "\"center\":" + center
                + ", \"radius\":\"" + radius + "\""
                + "}";
    }
}
record LineProperty(Vector start, Vector end) {
    @Override
    public String toString() {
        return "{"
                + "\"start\":" + start
                + ", \"end\":" + end
                + "}";
    }
}
record RectProperty(double width, double height, double x, double y, Transform transform) {
    @Override
    public String toString() {
        return "{"
                + "\"width\":\"" + width + "\""
                + ", \"height\":\"" + height + "\""
                + ", \"x\":\"" + x + "\""
                + ", \"y\":\"" + y + "\""
                + ", \"transform\":" + transform
                + "}";
    }
}
