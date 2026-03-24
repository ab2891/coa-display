package com.ab2891.coadisplay;

import com.ab2891.coadisplay.chat.ChatCommandManager;
import com.ab2891.coadisplay.chat.ChatListener;
import com.ab2891.coadisplay.chat.ClientScheduler;
import com.ab2891.coadisplay.chat.CrownsCommand;
import com.ab2891.coadisplay.crown.CrownCache;
import com.ab2891.coadisplay.crown.CrownCompletionTracker;
import com.ab2891.coadisplay.crown.CrownReader;
import com.ab2891.coadisplay.crown.SkyblockNbt;
import com.ab2891.coadisplay.diana.DianaFeature;
import com.ab2891.coadisplay.hud.CrownHud;
import com.ab2891.coadisplay.stats.StatsManager;
import com.coadisplay.CoaDisplay;
import com.coadisplay.config.CoaConfig;
import com.coadisplay.config.CoaConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class CoaDisplayClient implements ClientModInitializer {
    public static CoaConfig CONFIG;
    public static final CrownCache CACHE = new CrownCache();
    private static final ChatCommandManager CHAT_CMDS = new ChatCommandManager();
    private static final ClientScheduler SCHEDULER = new ClientScheduler();
    public static DianaFeature DIANA = new DianaFeature();
    private static long lastCoins = Long.MIN_VALUE;
    private static KeyBinding DIANA_DEBUG_KEY;

    @Override
    public void onInitializeClient() {
        StatsManager.init();
        CHAT_CMDS.register(new CrownsCommand());
        new ChatListener(CHAT_CMDS, SCHEDULER).register();
        CONFIG = CoaConfigManager.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            SCHEDULER.tick(client);

            while (DIANA_DEBUG_KEY != null && DIANA_DEBUG_KEY.wasPressed()) {
                if (DIANA == null || client.player == null) continue;
                Vec3d origin = new Vec3d(client.player.getX(), client.player.getY() + 2.0, client.player.getZ());
                Vec3d dir = client.player.getRotationVec(1.0f);
                DIANA.debugInject(origin, dir);
            }

            if (DIANA != null) {
                DIANA.onTick(client);
            }

            if (!CoaDisplayClient.CONFIG.enabled) {
                return;
            }

            Long opt = CrownReader.readCoinsConsumedFromHelmet(client.player);
            if (opt == null) {
                CACHE.setCoinsConsumed(-1L);
                lastCoins = Long.MIN_VALUE;
                return;
            }

            ItemStack helmet = client.player.getEquippedStack(EquipmentSlot.HEAD);
            String uuid = SkyblockNbt.getUniqueItemID(helmet);
            long coins = opt;

            if (coins != lastCoins) {
                lastCoins = coins;
                CACHE.setCoinsConsumed(coins);
            }

            if (uuid != null) {
                CrownCompletionTracker.onCrownCoinsRead(uuid, coins);
            }
            StatsManager.tickAutoSave();
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                CoaDisplay.id("crown_hud"),
                (context, tickCounter) -> CrownHud.render(context, tickCounter, CONFIG, CACHE)
        );
    }
}
