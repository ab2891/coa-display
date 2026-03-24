package com.ab2891.coadisplay.config;

import com.ab2891.coadisplay.CoaDisplayClient;
import com.coadisplay.config.CoaConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class HudMoveScreen extends Screen {
    private final Screen parent;
    private static final String PREVIEW = "Crown: 123,456,789";
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudMoveScreen(Screen parent) {
        super(Text.literal("Move Crown HUD"));
        this.parent = parent;
    }

    @Override
    public void close() {
        CoaConfigManager.save(CoaDisplayClient.CONFIG);
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,
                "Drag the text to reposition. ESC to save & exit.", this.width / 2, 15, -1);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer tr = client.textRenderer;
        int textW = tr.getWidth(PREVIEW);
        int textH = 9;

        int x = CoaDisplayClient.CONFIG.x;
        int y = CoaDisplayClient.CONFIG.y;
        x = clamp(x, 0, Math.max(0, this.width - textW));
        y = clamp(y, 0, Math.max(0, this.height - textH));
        CoaDisplayClient.CONFIG.x = x;
        CoaDisplayClient.CONFIG.y = y;

        int pad = 2;
        ctx.fill(x - pad, y - pad, x + textW + pad, y + textH + pad, Integer.MIN_VALUE);
        ctx.drawTextWithShadow(tr, PREVIEW, x, y, -1);
        ctx.drawTextWithShadow(tr, "x=" + x + " y=" + y, 5, this.height - 15, -1);
    }

    // TODO: In 1.21.8, mouse/key input methods use new record-based input types
    // (KeyInput, Click). The exact method signatures may need adjustment
    // based on the Yarn mappings for this version. The decompiled code used
    // class_11908 (KeyInput) and class_11909 (Click) with methods like
    // method_74245() -> button(), comp_4798() -> x(), comp_4799() -> y(),
    // comp_4795() -> key(). If these don't compile, adjust to match the actual
    // Yarn-mapped Screen method signatures for 1.21.8.

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        if (click.button() != 0) {
            return super.mouseClicked(click, doubleClick);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer tr = client.textRenderer;
        int x = CoaDisplayClient.CONFIG.x;
        int y = CoaDisplayClient.CONFIG.y;
        double mouseX = click.x();
        double mouseY = click.y();
        int textW = tr.getWidth(PREVIEW);
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
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (this.dragging && click.button() == 0) {
            CoaDisplayClient.CONFIG.x = (int) click.x() - this.dragOffsetX;
            CoaDisplayClient.CONFIG.y = (int) click.y() - this.dragOffsetY;
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0) {
            this.dragging = false;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == 256) {
            this.close();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    private static int clamp(int v, int min, int max) {
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }
}
