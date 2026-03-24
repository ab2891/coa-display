package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public record Ray(Vec3d origin, Vec3d dir) {
    public Ray {
        dir = dir.normalize();
    }
}
