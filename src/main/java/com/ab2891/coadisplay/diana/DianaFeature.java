package com.ab2891.coadisplay.diana;

import com.ab2891.coadisplay.diana.guess.GuessSession;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class DianaFeature {
    private final GuessSession session = new GuessSession();

    public void onParticlePacket(ParticleS2CPacket packet) {
        this.session.onParticle(packet);
    }

    public void onTick(MinecraftClient client) {
        this.session.onTick(client);
    }

    public void onSpadeUse() {
        this.session.beginAttempt();
    }

    public void onBurrowDug(BlockPos dug) {
        this.session.onBurrowDug(dug);
    }

    public void debugInject(Vec3d origin, Vec3d dir) {
        this.session.debugInjectSyntheticArrow(origin, dir);
    }
}
