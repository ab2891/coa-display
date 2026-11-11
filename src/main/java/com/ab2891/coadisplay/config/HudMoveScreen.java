package com.ab2891.coadisplay.config;

import com.ab2891.coadisplay.CoaDisplayClient;
import com.coadisplay.config.CoaConfigManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class HudMoveScreen extends Screen {
    private final Screen parent;
    private static final String PREVIEW = "Crown: 123,456,789";
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudMoveScreen(Screen parent) {
        super(Component.literal("Move Crown HUD"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        CoaConfigManager.save(CoaDisplayClient.CONFIG);
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        ctx.centeredText(Minecraft.getInstance().font,
                "Drag the text to reposition. ESC to save & exit.", this.width / 2, 15, -1);

        Minecraft client = Minecraft.getInstance();
        Font tr = client.font;
        int textW = tr.width(PREVIEW);
        int textH = 9;

        int x = CoaDisplayClient.CONFIG.x;
        int y = CoaDisplayClient.CONFIG.y;
        x = clamp(x, 0, Math.max(0, this.width - textW));
        y = clamp(y, 0, Math.max(0, this.height - textH));
        CoaDisplayClient.CONFIG.x = x;
        CoaDisplayClient.CONFIG.y = y;

        int pad = 2;
        ctx.fill(x - pad, y - pad, x + textW + pad, y + textH + pad, Integer.MIN_VALUE);
        ctx.text(tr, PREVIEW, x, y, -1);
        ctx.text(tr, "x=" + x + " y=" + y, 5, this.height - 15, -1);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
        if (click.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(click, doubleClick);
        }
        Minecraft client = Minecraft.getInstance();
        Font tr = client.font;
        int x = CoaDisplayClient.CONFIG.x;
        int y = CoaDisplayClient.CONFIG.y;
        double mouseX = click.x();
        double mouseY = click.y();
        int textW = tr.width(PREVIEW);
        int textH = 9;

        if (mouseX >= x && mouseX <= x + textW && mouseY >= y && mouseY <= y + textH) {
            this.dragging = true;
            this.dragOffsetX = (int) mouseX - x;
            this.dragOffsetY = (int) mouseY - y;
            return true;
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        if (this.dragging && click.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            CoaDisplayClient.CONFIG.x = (int) click.x() - this.dragOffsetX;
            CoaDisplayClient.CONFIG.y = (int) click.y() - this.dragOffsetY;
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (click.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.dragging = false;
        }
        return super.mouseReleased(click);
    }

    private static int clamp(int v, int min, int max) {
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }
}
