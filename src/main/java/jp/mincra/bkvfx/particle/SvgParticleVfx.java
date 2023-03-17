package jp.mincra.bkvfx.particle;

import jp.mincra.ezsvg.elements.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.ParticleData;

import java.awt.*;
import java.util.List;

public class SvgParticleVfx extends ParticleVfx {
    private ParticleEffect particleEffect;
    private int amount;
    private float speed;
    private Vector offset;
    private Color color;
    private ParticleData particleData;

    /**
     *
     * @param svgObject 描画するパーティクルのSvgObject
     */
    //TODO: パーティクルの中心を(0,0)に設定する
    public SvgParticleVfx(SvgObject svgObject, double density, ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
        List<SvgElement> svgElements = svgObject.getSvgElements();
        // heightとwidth, 大きい方をsizeにする
        float size = svgObject.getHeight();
        float width = svgObject.getWidth();
        if (width > size) size = width;
        double _size = 1 / size;

        for (SvgElement svgElement : svgElements) {
            if (svgElement instanceof Circle) {
                Circle circle = (Circle) svgElement;
                Vector center = new Vector(circle.getCenterX() * _size, circle.getCenterY() * _size, 0);
                double radius = circle.getRadius() * _size;
                circle(center, radius, density);
            } else if (svgElement instanceof Path) {
                Path path = (Path) svgElement;
                List<PathElement> pathElements = path.getPaths();
                float beforeX = 0;
                float beforeY = 0;
                for (PathElement pathElement : pathElements) {
                    switch (pathElement.pathType()) {
                        case M:
                            break;
                        case L:
                            Vector start = new Vector(beforeX, beforeY, 0);
                            Vector end = new Vector(pathElement.x(), pathElement.y(), 0);
                            line(start, end, density);
                            break;
                        case Z:
                            break;
                    }
                    beforeX = pathElement.x();
                    beforeY = pathElement.y();
                }
            }
        }
    }

    public SvgParticleVfx setParticleEffect(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
        return this;
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

    public SvgParticleVfx setParticleData(ParticleData particleData) {
        this.particleData = particleData;
        return this;
    }

    @Override
    public void playEffect(Location center, double scale) {
        Bukkit.getLogger().info("Play effect in SvgParticleVfx");
        for (Particle particle : particles) {
            ParticleBuilder builder =
                    new ParticleBuilder(particleEffect, center.add(particle.getLocation()));

            Vector offset = particle.getOffset();
            int amount = particle.getAmount();
            float speed = particle.getSpeed();
            Color color = particle.getColor();
            ParticleData particleData = particle.getParticleData();

            if (offset != null) {
                builder = builder.setOffset(offset);
            } else {
                builder = builder.setOffset(this.offset);
            }

            if (amount != 0) {
                builder = builder.setAmount(amount);
            } else {
                builder = builder.setAmount(this.amount);
            }

            if (speed != 0) {
                builder = builder.setSpeed(speed);
            } else {
                builder = builder.setSpeed(this.speed);
            }

            if (color != null) {
                builder = builder.setColor(color);
            } else {
                builder = builder.setColor(this.color);
            }

            if (particleData != null) {
                builder = builder.setParticleData(particleData);
            } else {
                builder = builder.setParticleData(this.particleData);
            }

            builder.display();
        }
    }
}
