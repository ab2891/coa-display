package com.ab2891.coadisplay.crown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class SkyblockNbt {
    private SkyblockNbt() {
    }

    @Nullable
    public static NbtCompound getCustomData(ItemStack stack) {
        NbtComponent custom = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        if (custom.isEmpty()) {
            return null;
        }
        return custom.copyNbt();
    }

    @Nullable
    public static Long getCollectedCoins(ItemStack stack) {
        NbtCompound root = getCustomData(stack);
        if (root == null) {
            return null;
        }
        return root.getLong("collected_coins").orElse(null);
    }

    @Nullable
    public static String getSkyblockId(ItemStack stack) {
        NbtCompound ea = getCustomData(stack);
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
        NbtCompound ea = getCustomData(stack);
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
        NbtComponent custom = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        if (custom.isEmpty()) {
            System.out.println("[CoA] CUSTOM_DATA is empty");
            return;
        }
        NbtCompound root = custom.copyNbt();
        System.out.println("[CoA] CUSTOM_DATA root=" + root);
    }
}
