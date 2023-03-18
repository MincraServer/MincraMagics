package jp.mincra.bkvfx;

import org.bukkit.util.Vector;

public final class Quaternion {
    private double x;
    private double y;
    private double z;
    private double w;
    //private float[] matrixs;

    public Quaternion(final Quaternion q) {
        this(q.x, q.y, q.z, q.w);
    }

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(final Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public Quaternion(Vector axis, double angle) {
        set(axis, angle);
    }

    /**
     * ノルム
     * @return
     */
    public double norm() {
        return Math.sqrt(dot(this));
    }

    public double x() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double w() {
        return w;
    }

    /**
     * @param axis  rotation axis, unit vector
     * @param angle the rotation angle
     * @return this
     */
    public Quaternion set(Vector axis, double angle) {
        //matrixs = null;
        double sin = Math.sin(angle / 2);
        w = Math.cos(angle / 2);
        x = axis.getX() * sin;
        y = axis.getY() * sin;
        z = axis.getZ() * sin;
        return this;
    }

    public Quaternion multiply(Quaternion q) {
        // (x, y, z, w) = (qw*x−qz*y+qy*z+qx*w,
        //                 qz*x+qw*y−qx*z+qy*w,
        //                 −qy*x+qx*y+qw*z+qz*w,
        //                 −qx*x−qy*y−qz*z+qw*w)
        double nx = w * q.x + x * q.w - y * q.z + z * q.y;
        double ny = w * q.y + y * q.w - z * q.x + x * q.z;
        double nz = w * q.z + z * q.w - x * q.y + y * q.x;
        w = w * q.w - x * q.x - y * q.y - z * q.z;
        x = nx;
        y = ny;
        z = nz;
        return this;
    }

    public Quaternion multiply(double scale) {
        if (scale != 1) {
            w *= scale;
            x *= scale;
            y *= scale;
            z *= scale;
        }
        return this;
    }

    public Quaternion div(double scale) {
        if (scale != 1 && scale != 0) {
            w /= scale;
            x /= scale;
            y /= scale;
            z /= scale;
        }
        return this;
    }

    public double dot(Quaternion q) {
        return x * q.x + y * q.y + z * q.z + w * q.w;
    }

    public boolean equals(Quaternion q) {
        return x == q.x && y == q.y && z == q.z && w == q.w;
    }

    public Quaternion interpolate(Quaternion q, double t) {
        if (!equals(q)) {
            double d = dot(q);
            double qx, qy, qz, qw;

            if (d < 0f) {
                qx = -q.x;
                qy = -q.y;
                qz = -q.z;
                qw = -q.w;
                d = -d;
            } else {
                qx = q.x;
                qy = q.y;
                qz = q.z;
                qw = q.w;
            }

            double f0, f1;

            if ((1 - d) > 0.1f) {
                double angle = Math.acos(d);
                double s = Math.sin(angle);
                double tAngle = t * angle;
                f0 = Math.sin(angle - tAngle) / s;
                f1 = Math.sin(tAngle) / s;
            } else {
                f0 = 1 - t;
                f1 = t;
            }

            x = f0 * x + f1 * qx;
            y = f0 * y + f1 * qy;
            z = f0 * z + f1 * qz;
            w = f0 * w + f1 * qw;
        }

        return this;
    }

    public Quaternion normalize() {
        return div(norm());
    }

    /**
     * Converts this Quaternion into a matrix, returning it as a float array.
     */
    public float[] toMatrix() {
        float[] matrixs = new float[16];
        toMatrix(matrixs);
        return matrixs;
    }

    /**
     * Converts this Quaternion into a matrix, placing the values into the given array.
     *
     * @param matrixs 16-length float array.
     */
    public final void toMatrix(float[] matrixs) {
        matrixs[3] = 0.0f;
        matrixs[7] = 0.0f;
        matrixs[11] = 0.0f;
        matrixs[12] = 0.0f;
        matrixs[13] = 0.0f;
        matrixs[14] = 0.0f;
        matrixs[15] = 1.0f;

        matrixs[0] = (float) (1.0f - (2.0f * ((y * y) + (z * z))));
        matrixs[1] = (float) (2.0f * ((x * y) - (z * w)));
        matrixs[2] = (float) (2.0f * ((x * z) + (y * w)));

        matrixs[4] = (float) (2.0f * ((x * y) + (z * w)));
        matrixs[5] = (float) (1.0f - (2.0f * ((x * x) + (z * z))));
        matrixs[6] = (float) (2.0f * ((y * z) - (x * w)));

        matrixs[8] = (float) (2.0f * ((x * z) - (y * w)));
        matrixs[9] = (float) (2.0f * ((y * z) + (x * w)));
        matrixs[10] = (float) (1.0f - (2.0f * ((x * x) + (y * y))));
    }

    /**
     * 共役四元数を返す
     * @return 共役四元数
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Vector rotate(Vector vec) {
        return new Quaternion(this).multiply(new Quaternion(vec.getX(), vec.getY(), vec.getZ(), 0)).multiply(this.conjugate()).toVector();
    }
}