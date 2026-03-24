package com.coadisplay;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public final class CoaDisplay implements ModInitializer {
    public static final String MOD_ID = "coadisplay";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
    }
}
