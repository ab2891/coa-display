package com.ab2891.coadisplay.chat;

import com.ab2891.coadisplay.stats.StatsManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public final class CrownsCommand implements ChatCommand {
    @Override
    public String trigger() {
        return "!crown";
    }

    @Override
    public void run(MinecraftClient client, String senderPlain, String[] args) {
        if (client.getNetworkHandler() == null) {
            return;
        }
        int completed = StatsManager.get().crownsCompleted;
        client.getNetworkHandler().sendChatCommand("pc Crowns completed: " + completed);
    }
}
