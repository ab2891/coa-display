package com.ab2891.coadisplay.diana;

import com.ab2891.coadisplay.diana.guess.GuessSession;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class DianaFeature {
    private final GuessSession session = new GuessSession();

    public void onParticlePacket(ClientboundLevelParticlesPacket packet) {
        this.session.onParticle(packet);
    }

    public void onTick(Minecraft client) {
        this.session.onTick(client);
    }

    public void onSpadeUse() {
        this.session.beginAttempt();
    }

    public void onBurrowDug(BlockPos dug) {
        this.session.onBurrowDug(dug);
    }

    public void debugInject(Vec3 origin, Vec3 dir) {
        this.session.debugInjectSyntheticArrow(origin, dir);
    }
}
