package jp.mincra.bkvfx.particle;

import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.ParticleData;

import java.awt.*;

public class Particle {
    private ParticleEffect particle;
    private Vector location;
    private Vector offset;
    private int amount;
    private float speed;
    private Color color;
    private ParticleData particleData;

    public ParticleEffect getParticle() {
        return particle;
    }

    public void setParticle(ParticleEffect particle) {
        this.particle = particle;
    }

    public Vector getLocation() {
        return location;
    }

    public void setLocation(Vector location) {
        this.location = location;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public void setParticleData(ParticleData particleData) {
        this.particleData = particleData;
    }

    @Override
    public String toString() {
        return "{"
                + "\"location\":\"" + location + "\""
                + ", \"amount\":\"" + amount + "\""
                + ", \"speed\":\"" + speed + "\""
                + "}";
    }
}
