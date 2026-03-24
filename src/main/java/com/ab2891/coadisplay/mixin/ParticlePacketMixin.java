package com.ab2891.coadisplay.mixin;

import com.ab2891.coadisplay.CoaDisplayClient;
import com.ab2891.coadisplay.diana.DianaFeature;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ParticlePacketMixin {
    @Inject(method = "onParticle", at = @At("HEAD"))
    private void coadisplay$onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        DianaFeature diana = CoaDisplayClient.DIANA;
        if (diana != null) {
            diana.onParticlePacket(packet);
        }
    }
}
