package com.ab2891.coadisplay.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class ChatListener {
    private final ChatCommandManager manager;
    private final ClientScheduler scheduler;

    public ChatListener(ChatCommandManager manager, ClientScheduler scheduler) {
        this.manager = manager;
        this.scheduler = scheduler;
    }

    public void register() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String plain = message.getString().trim();
            plain = stripWeirdFormatting(plain).trim();
            if (plain == null) {
                return;
            }
            if (!plain.contains("Party")) {
                return;
            }
            Minecraft client = Minecraft.getInstance();
            if (client.player == null || client.getConnection() == null) {
                return;
            }
            this.manager.onPartyChatLine(client, plain, this.scheduler);
        });
    }

    private static String stripWeirdFormatting(String s) {
        return s.replaceAll("\u252c\u00ba.", "");
    }
}
