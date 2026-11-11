package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public record Ray(Vec3 origin, Vec3 dir) {
    public Ray {
        dir = dir.normalize();
    }
}
