package com.ab2891.coadisplay.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class ChatCommandManager {
    private final Map<String, ChatCommand> commands = new HashMap<>();
    private long nextAllowedMs = 0L;
    private static final long COOLDOWN_MS = 1000L;

    public void register(ChatCommand command) {
        this.commands.put(command.trigger().toLowerCase(), command);
    }

    public void onPartyChatLine(MinecraftClient client, String plainLine, ClientScheduler scheduler) {
        int colon = plainLine.lastIndexOf(":");
        if (colon < 0) {
            return;
        }
        String content = plainLine.substring(colon + 1).trim();
        if (content.isEmpty()) {
            return;
        }
        String[] parts = content.split("\\s+");
        String trig = parts[0].toLowerCase();
        ChatCommand cmd = this.commands.get(trig);
        if (cmd == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < this.nextAllowedMs) {
            return;
        }
        this.nextAllowedMs = now + COOLDOWN_MS;
        String sender = plainLine.substring(0, colon).trim();
        String[] args = parts.length <= 1 ? new String[]{} : Arrays.copyOfRange(parts, 1, parts.length);
        scheduler.schedule(now + 400L, () -> cmd.run(client, sender, args));
    }
}
