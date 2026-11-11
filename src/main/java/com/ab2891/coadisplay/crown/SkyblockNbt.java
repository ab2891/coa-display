package com.ab2891.coadisplay.crown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class SkyblockNbt {
    private SkyblockNbt() {
    }

    @Nullable
    public static CompoundTag getCustomData(ItemStack stack) {
        CustomData custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (custom.isEmpty()) {
            return null;
        }
        return custom.copyTag();
    }

    @Nullable
    public static Long getCollectedCoins(ItemStack stack) {
        CompoundTag root = getCustomData(stack);
        if (root == null) {
            return null;
        }
        return root.getLong("collected_coins").orElse(null);
    }

    @Nullable
    public static String getSkyblockId(ItemStack stack) {
        CompoundTag ea = getCustomData(stack);
        if (ea == null) {
            return null;
        }
        return ea.getString("id").orElse(null);
    }

    public static boolean hasSkyblockId(ItemStack stack, String expectedId) {
        String id = getSkyblockId(stack);
        return expectedId.equals(id);
    }

    @Nullable
    public static String getUniqueItemID(ItemStack stack) {
        CompoundTag ea = getCustomData(stack);
        if (ea == null) {
            return null;
        }
        String uuid = ea.getString("uuid").orElse(null);
        if (uuid != null) {
            return uuid;
        }
        return null;
    }

    public static void debugCustomData(ItemStack stack) {
        CustomData custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (custom.isEmpty()) {
            System.out.println("[CoA] CUSTOM_DATA is empty");
            return;
        }
        CompoundTag root = custom.copyTag();
        System.out.println("[CoA] CUSTOM_DATA root=" + root);
    }
}
