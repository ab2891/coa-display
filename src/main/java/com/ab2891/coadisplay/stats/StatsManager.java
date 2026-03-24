package com.ab2891.coadisplay.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public final class StatsManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIR_NAME = "coadisplay";
    private static final String FILE_NAME = "stats.json";
    private static Path statsPath;
    private static CrownStats stats = new CrownStats();
    private static boolean dirty = false;
    private static long nextAllowedSaveMs = 0L;
    private static final long AUTOSAVE_INTERVAL_MS = 20000L;

    private StatsManager() {
    }

    public static void init() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path modDir = configDir.resolve(DIR_NAME);
        statsPath = modDir.resolve(FILE_NAME);
        try {
            Files.createDirectories(modDir);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        load();
    }

    public static CrownStats get() {
        return stats;
    }

    public static void markDirty() {
        dirty = true;
    }

    public static void tickAutoSave() {
        if (!dirty || statsPath == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < nextAllowedSaveMs) {
            return;
        }
        save();
        dirty = false;
        nextAllowedSaveMs = now + AUTOSAVE_INTERVAL_MS;
    }

    public static void load() {
        if (statsPath == null) {
            return;
        }
        if (!Files.exists(statsPath)) {
            stats = new CrownStats();
            save();
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(statsPath, StandardCharsets.UTF_8)) {
            CrownStats loaded = GSON.fromJson(r, CrownStats.class);
            stats = loaded != null ? loaded : new CrownStats();
            if (stats.lastCoinsByUUID == null) {
                stats.lastCoinsByUUID = new HashMap<>();
            }
        } catch (JsonSyntaxException badJson) {
            backupBrokenStatsFile();
            stats = new CrownStats();
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (statsPath == null) {
            return;
        }
        Path tmp = statsPath.resolveSibling("stats.json.tmp");
        try (BufferedWriter w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(stats, w);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            Files.move(tmp, statsPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            try {
                Files.move(tmp, statsPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void backupBrokenStatsFile() {
        try {
            Path backup = statsPath.resolveSibling("stats.json.broken");
            Files.move(statsPath, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
    }
}
