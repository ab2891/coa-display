package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;

@Environment(EnvType.CLIENT)
public class PacketFilters {
    private PacketFilters() {
    }

    private static boolean approx(float a, float b, float eps) {
        return Math.abs(a - b) <= eps;
    }

    public static boolean isArrowDust(ClientboundLevelParticlesPacket p) {
        if (p.getParticle().getType() != ParticleTypes.DUST) {
            return false;
        }
        if (!(p.getParticle() instanceof DustParticleOptions)) {
            return false;
        }
        if (p.getCount() != 0) {
            return false;
        }
        return approx(p.getMaxSpeed(), 1.0f, 1.0E-4f);
    }

    public static boolean isSpadeLava(ClientboundLevelParticlesPacket p) {
        if (p.getParticle().getType() != ParticleTypes.LAVA) {
            return false;
        }
        if (p.getCount() != 2) {
            return false;
        }
        return approx(p.getMaxSpeed(), -0.5f, 1.0E-4f);
    }

    public static IntRange arrowRange(ClientboundLevelParticlesPacket p) {
        float ox = p.getXDist();
        float oy = p.getYDist();
        if (approx(oy, 128.0f, 1.0E-4f)) {
            return new IntRange(0, 117);
        }
        if (approx(oy, 255.0f, 1.0E-4f) && approx(ox, 255.0f, 1.0E-4f)) {
            return new IntRange(112, 282);
        }
        if (approx(ox, 255.0f, 1.0E-4f)) {
            return new IntRange(281, 600);
        }
        return null;
    }
}
