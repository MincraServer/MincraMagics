package jp.mincra.bkvfx;

import java.util.Objects;

public final class Vector3 {
    private double x,y,z;

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Vector3(double ix, double iy, double iz) {
        x = ix;
        y = iy;
        z = iz;
    }

    public void set(double ix, double iy, double iz) {
        x = ix;
        y = iy;
        z = iz;
    }

    public double magnitude() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public void multiply(double f) {
        x *= f;
        y *= f;
        z *= f;
    }

    public void normalise() {
        double mag = magnitude();
        x /= mag;
        y /= mag;
        z /= mag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Double.compare(vector3.x, x) == 0 && Double.compare(vector3.y, y) == 0 && Double.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "{"
                + "\"x\":\"" + x + "\""
                + ", \"y\":\"" + y + "\""
                + ", \"z\":\"" + z + "\""
                + "}";
    }
}