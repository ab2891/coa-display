package com.ab2891.coadisplay.hud;

import com.ab2891.coadisplay.crown.CrownCache;
import com.coadisplay.config.CoaConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.text.NumberFormat;

@Environment(EnvType.CLIENT)
public final class CrownHud {
    private static final NumberFormat NF = NumberFormat.getInstance();

    private CrownHud() {
    }

    public static void render(DrawContext ctx, RenderTickCounter tickCounter, CoaConfig cfg, CrownCache cache) {
        if (!cfg.enabled) {
            return;
        }
        long coins = cache.getCoinsConsumed();
        if (coins < 0L) {
            return;
        }
        String value = NF.format(coins);
        String text = cfg.showLabel ? "Crown: " + value : value;
        ctx.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, cfg.x, cfg.y, -1);
    }
}
