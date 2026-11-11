package com.ab2891.coadisplay.mixin;

import com.ab2891.coadisplay.CoaDisplayClient;
import com.ab2891.coadisplay.diana.DianaFeature;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public class ParticlePacketMixin {
    @Inject(method = "handleParticleEvent", at = @At("RETURN"))
    private void coadisplay$onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        DianaFeature diana = CoaDisplayClient.DIANA;
        if (diana != null) {
            diana.onParticlePacket(packet);
        }
    }
}
