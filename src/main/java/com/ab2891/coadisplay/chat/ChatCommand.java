package com.ab2891.coadisplay.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public interface ChatCommand {
    String trigger();

    void run(Minecraft client, String sender, String[] args);
}
