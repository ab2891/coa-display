package com.ab2891.coadisplay.crown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class CrownReader {
    private CrownReader() {
    }

    public static Long readCoinsConsumedFromHelmet(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return null;
        }
        String name = helmet.getName().getString().toLowerCase();
        if (!name.contains("crown") || !name.contains("avarice")) {
            return null;
        }
        return readCoinsConsumedFromLore(helmet);
    }

    private static Long readCoinsConsumedFromLore(ItemStack stack) {
        return SkyblockNbt.getCollectedCoins(stack);
    }
}
