package com.ab2891.coadisplay.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public final class TickHook {
    public static void register(ClientScheduler scheduler) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> scheduler.tick(client));
    }
}
