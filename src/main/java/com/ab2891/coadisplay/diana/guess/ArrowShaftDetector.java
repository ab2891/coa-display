package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * Detects the arrow shaft direction from a set of dust particle positions using
 * PCA (Principal Component Analysis) via power iteration on the covariance matrix.
 * This is intentional -- the PCA approach fits the arrow shaft better than alternatives.
 */
@Environment(EnvType.CLIENT)
public final class ArrowShaftDetector {

    public Ray tryDetectRay(List<Vec3> points) {
        if (points.size() < 6) {
            return null;
        }
        Vec3 mean = mean(points);
        double[][] cov = covariance(points, mean);
        Vec3 dir = powerIter(cov);
        if (dir == null || dir.lengthSqr() < 1.0E-12) {
            return null;
        }
        dir = dir.normalize();

        double minT = Double.POSITIVE_INFINITY;
        double maxT = Double.NEGATIVE_INFINITY;
        for (Vec3 p : points) {
            double t = p.subtract(mean).dot(dir);
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

        Vec3 base = mean.add(dir.scale(minT)).add(0.0, 1.5, 0.0);
        Vec3 tip = mean.add(dir.scale(maxT)).add(0.0, 1.5, 0.0);
        Vec3 rayDir = tip.subtract(base);
        if (rayDir.lengthSqr() < 1.0E-12) {
            return null;
        }
        return new Ray(base, rayDir);
    }

    private static Vec3 mean(List<Vec3> pts) {
        double sx = 0.0;
        double sy = 0.0;
        double sz = 0.0;
        for (Vec3 p : pts) {
            sx += p.x;
            sy += p.y;
            sz += p.z;
        }
        double n = pts.size();
        return new Vec3(sx / n, sy / n, sz / n);
    }

    private static double[][] covariance(List<Vec3> pts, Vec3 m) {
        double cxx = 0.0;
        double cxy = 0.0;
        double cxz = 0.0;
        double cyy = 0.0;
        double cyz = 0.0;
        double czz = 0.0;
        for (Vec3 p : pts) {
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

    private static Vec3 powerIter(double[][] a) {
        Vec3 v = new Vec3(1.0, 1.0, 1.0).normalize();
        for (int i = 0; i < 30; ++i) {
            Vec3 w = mul(a, v);
            double len2 = w.lengthSqr();
            if (len2 < 1.0E-18) {
                return null;
            }
            v = w.normalize();
        }
        return v;
    }

    private static Vec3 mul(double[][] a, Vec3 v) {
        double x = a[0][0] * v.x + a[0][1] * v.y + a[0][2] * v.z;
        double y = a[1][0] * v.x + a[1][1] * v.y + a[1][2] * v.z;
        double z = a[2][0] * v.x + a[2][1] * v.y + a[2][2] * v.z;
        return new Vec3(x, y, z);
    }
}
