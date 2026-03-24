package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;

@Environment(EnvType.CLIENT)
public class PacketFilters {
    private PacketFilters() {
    }

    private static boolean approx(float a, float b, float eps) {
        return Math.abs(a - b) <= eps;
    }

    public static boolean isArrowDust(ParticleS2CPacket p) {
        if (p.getParameters().getType() != ParticleTypes.DUST) {
            return false;
        }
        if (!(p.getParameters() instanceof DustParticleEffect)) {
            return false;
        }
        if (p.getCount() != 0) {
            return false;
        }
        return approx(p.getSpeed(), 1.0f, 1.0E-4f);
    }

    public static boolean isSpadeLava(ParticleS2CPacket p) {
        if (p.getParameters().getType() != ParticleTypes.LAVA) {
            return false;
        }
        if (p.getCount() != 2) {
            return false;
        }
        return approx(p.getSpeed(), -0.5f, 1.0E-4f);
    }

    public static IntRange arrowRange(ParticleS2CPacket p) {
        float ox = p.getOffsetX();
        float oy = p.getOffsetY();
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
