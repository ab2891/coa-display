package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class CandidateScanner {
    private static final double STEP = 1.0;

    public List<Candidate> scan(Level world, Ray ray, IntRange range) {
        ArrayList<Candidate> out = new ArrayList<>(256);
        for (double d = (double) range.min(); d <= (double) range.max(); d += STEP) {
            Vec3 p = ray.origin().add(ray.dir().scale(d));
            int x = (int) Math.floor(p.x);
            int z = (int) Math.floor(p.z);
            boolean loaded = world.hasChunkAt(new BlockPos(x, 0, z));
            if (loaded) {
                BlockPos topAir = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
                BlockPos ground = topAir.below();
                Vec3 center = new Vec3(
                        (double) ground.getX() + 0.5,
                        (double) ground.getY() + 0.5,
                        (double) ground.getZ() + 0.5
                );
                double dist3D = distancePointToRay(center, ray);
                double score = dist3D * 500000.0 / Math.max(d, 1.0E-6);
                out.add(new Candidate(ground, true, score, d));
            } else {
                double dist2D = distancePointToRayXZ((double) x + 0.5, (double) z + 0.5, ray);
                double score = dist2D * 500000.0 / Math.max(d, 1.0E-6);
                out.add(new Candidate(new BlockPos(x, 0, z), false, score, d));
            }
        }
        return out;
    }

    private double distancePointToRay(Vec3 point, Ray ray) {
        Vec3 v = point.subtract(ray.origin());
        double t = v.dot(ray.dir());
        Vec3 proj = ray.origin().add(ray.dir().scale(t));
        return point.distanceTo(proj);
    }

    private double distancePointToRayXZ(double px, double pz, Ray ray) {
        double ox = ray.origin().x;
        double oz = ray.origin().z;
        double dx = ray.dir().x;
        double dz = ray.dir().z;
        double len2 = dx * dx + dz * dz;
        if (len2 < 1.0E-12) {
            return Double.POSITIVE_INFINITY;
        }
        double vx = px - ox;
        double vz = pz - oz;
        double t = (vx * dx + vz * dz) / len2;
        double qx = ox + t * dx;
        double qz = oz + t * dz;
        double ex = px - qx;
        double ez = pz - qz;
        return Math.sqrt(ex * ex + ez * ez);
    }
}
