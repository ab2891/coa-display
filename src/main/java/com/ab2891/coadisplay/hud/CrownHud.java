package com.ab2891.coadisplay.hud;

import com.ab2891.coadisplay.crown.CrownCache;
import com.coadisplay.config.CoaConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.text.NumberFormat;

@Environment(EnvType.CLIENT)
public final class CrownHud {
    private static final NumberFormat NF = NumberFormat.getInstance();

    private CrownHud() {
    }

    public static void render(GuiGraphicsExtractor ctx, DeltaTracker tickCounter, CoaConfig cfg, CrownCache cache) {
        if (!cfg.enabled) {
            return;
        }
        long coins = cache.getCoinsConsumed();
        if (coins < 0L) {
            return;
        }
        String value = NF.format(coins);
        String text = cfg.showLabel ? "Crown: " + value : value;
        ctx.text(Minecraft.getInstance().font, text, cfg.x, cfg.y, -1);
    }
}
