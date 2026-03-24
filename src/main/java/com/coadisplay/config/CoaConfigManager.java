package com.coadisplay.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CoaConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("coadisplay.json");

    private CoaConfigManager() {
    }

    public static CoaConfig load() {
        if (!Files.exists(FILE)) {
            return new CoaConfig();
        }
        try (BufferedReader r = Files.newBufferedReader(FILE)) {
            CoaConfig cfg = GSON.fromJson(r, CoaConfig.class);
            return cfg != null ? cfg : new CoaConfig();
        } catch (IOException e) {
            return new CoaConfig();
        }
    }

    public static void save(CoaConfig cfg) {
        try {
            Files.createDirectories(FILE.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(FILE)) {
                GSON.toJson(cfg, w);
            }
        } catch (IOException ignored) {
        }
    }
}
