package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Detects the arrow shaft direction from a set of dust particle positions using
 * PCA (Principal Component Analysis) via power iteration on the covariance matrix.
 * This is intentional -- the PCA approach fits the arrow shaft better than alternatives.
 */
@Environment(EnvType.CLIENT)
public final class ArrowShaftDetector {

    public Ray tryDetectRay(List<Vec3d> points) {
        if (points.size() < 6) {
            return null;
        }
        Vec3d mean = mean(points);
        double[][] cov = covariance(points, mean);
        Vec3d dir = powerIter(cov);
        if (dir == null || dir.lengthSquared() < 1.0E-12) {
            return null;
        }
        dir = dir.normalize();

        double minT = Double.POSITIVE_INFINITY;
        double maxT = Double.NEGATIVE_INFINITY;
        for (Vec3d p : points) {
            double t = p.subtract(mean).dotProduct(dir);
            if (t < minT) {
                minT = t;
            }
            if (t > maxT) {
                maxT = t;
            }
        }
        if (maxT - minT < 0.5) {
            return null;
        }

        Vec3d base = mean.add(dir.multiply(minT)).add(0.0, 1.5, 0.0);
        Vec3d tip = mean.add(dir.multiply(maxT)).add(0.0, 1.5, 0.0);
        Vec3d rayDir = tip.subtract(base);
        if (rayDir.lengthSquared() < 1.0E-12) {
            return null;
        }
        return new Ray(base, rayDir);
    }

    private static Vec3d mean(List<Vec3d> pts) {
        double sx = 0.0;
        double sy = 0.0;
        double sz = 0.0;
        for (Vec3d p : pts) {
            sx += p.x;
            sy += p.y;
            sz += p.z;
        }
        double n = pts.size();
        return new Vec3d(sx / n, sy / n, sz / n);
    }

    private static double[][] covariance(List<Vec3d> pts, Vec3d m) {
        double cxx = 0.0;
        double cxy = 0.0;
        double cxz = 0.0;
        double cyy = 0.0;
        double cyz = 0.0;
        double czz = 0.0;
        for (Vec3d p : pts) {
            double x = p.x - m.x;
            double y = p.y - m.y;
            double z = p.z - m.z;
            cxx += x * x;
            cxy += x * y;
            cxz += x * z;
            cyy += y * y;
            cyz += y * z;
            czz += z * z;
        }
        double n = Math.max(1, pts.size() - 1);
        return new double[][]{
                {cxx /= n, cxy /= n, cxz /= n},
                {cxy, cyy /= n, cyz /= n},
                {cxz, cyz, czz /= n}
        };
    }

    private static Vec3d powerIter(double[][] a) {
        Vec3d v = new Vec3d(1.0, 1.0, 1.0).normalize();
        for (int i = 0; i < 30; ++i) {
            Vec3d w = mul(a, v);
            double len2 = w.lengthSquared();
            if (len2 < 1.0E-18) {
                return null;
            }
            v = w.normalize();
        }
        return v;
    }

    private static Vec3d mul(double[][] a, Vec3d v) {
        double x = a[0][0] * v.x + a[0][1] * v.y + a[0][2] * v.z;
        double y = a[1][0] * v.x + a[1][1] * v.y + a[1][2] * v.z;
        double z = a[2][0] * v.x + a[2][1] * v.y + a[2][2] * v.z;
        return new Vec3d(x, y, z);
    }
}
