package com.ab2891.coadisplay.crown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class CrownReader {
    private CrownReader() {
    }

    public static Long readCoinsConsumedFromHelmet(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return null;
        }
        String name = helmet.getHoverName().getString().toLowerCase();
        if (!name.contains("crown") || !name.contains("avarice")) {
            return null;
        }
        return readCoinsConsumedFromLore(helmet);
    }

    private static Long readCoinsConsumedFromLore(ItemStack stack) {
        return SkyblockNbt.getCollectedCoins(stack);
    }
}
