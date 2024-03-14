package jp.mincra.bkvfx.particle;

import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.awt.*;

public class ParticleData {
    private Particle particle;
    private Vector location;
    private Vector offset;
    private int amount;
    private float speed;
    private Color color;

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
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

    @Override
    public String toString() {
        return "{"
                + "\"location\":\"" + location + "\""
                + ", \"amount\":\"" + amount + "\""
                + ", \"speed\":\"" + speed + "\""
                + "}";
    }
}
