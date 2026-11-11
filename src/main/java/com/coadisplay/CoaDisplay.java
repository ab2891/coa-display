package com.coadisplay;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public final class CoaDisplay implements ModInitializer {
    public static final String MOD_ID = "coadisplay";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
    }
}
