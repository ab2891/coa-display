package com.ab2891.coadisplay.diana.guess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class GuessSession {
    private final CandidateScanner scanner = new CandidateScanner();
    private final CandidateRanker ranker = new HeuristicRanker();
    private BlockPos lastGuess = null;
    private double lastConfidence = 0.0;
    private IntRange range = null;
    private final ArrayList<Vec3d> dust = new ArrayList<>();
    private long lastDustMs = 0L;
    private final ArrowShaftDetector detector = new ArrowShaftDetector();
    private long lastPrintMs = 0L;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public void onTick(MinecraftClient client) {
        if (this.lastDustMs != 0L && System.currentTimeMillis() - this.lastDustMs > 1500L) {
            this.beginAttempt();
        }
    }

    public void onParticle(ParticleS2CPacket packet) {
        boolean arrow = PacketFilters.isArrowDust(packet);
        boolean lava = PacketFilters.isSpadeLava(packet);
        if (!arrow && !lava) {
            return;
        }

        if (arrow) {
            if (this.range == null) {
                this.range = PacketFilters.arrowRange(packet);
                if (this.range == null) {
                    return;
                }
            }
            Vec3d p = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
            this.dust.add(p);
            this.lastDustMs = System.currentTimeMillis();

            if (this.dust.size() < 20) {
                return;
            }

            Ray ray = this.detector.tryDetectRay(this.dust);
            if (ray == null) {
                return;
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            World world = mc.world;
            if (world == null) {
                return;
            }

            List<Candidate> candidates = this.scanner.scan(world, ray, this.range);
            if (candidates.isEmpty()) {
                return;
            }
            List<Candidate> ranked = this.ranker.rank(ray, this.range, candidates);
            this.lastGuess = ranked.get(0).pos;
            this.lastConfidence = this.ranker.confidence(ranked);
            System.out.println("[Diana] guess=" + this.lastGuess + " conf=" + this.lastConfidence
                    + " candidates=" + ranked.size());
            System.out.println("[Diana] Arrow ray detected. range=" + this.range
                    + " origin=" + ray.origin() + " dir=" + ray.dir());
        }

        if (lava) {
            System.out.println("[Diana] Spade lava packet detected at "
                    + packet.getX() + "," + packet.getY() + "," + packet.getZ());
        }
    }

    public void beginAttempt() {
        this.dust.clear();
        this.range = null;
        this.lastDustMs = 0L;
    }

    /**
     * Called when a burrow is dug. Logs the actual burrow position along with
     * collected particle data, arrow ray, and guess position to a JSON file
     * for future ML training data collection.
     */
    public void onBurrowDug(BlockPos dug) {
        if (dug == null) {
            return;
        }

        System.out.println("[Diana] Burrow dug at " + dug);

        // Compute the arrow ray from collected dust for logging purposes
        Ray ray = null;
        if (this.dust.size() >= 6) {
            ray = this.detector.tryDetectRay(this.dust);
        }

        // Calculate error distance if we had a guess
        double errorDistance = -1.0;
        if (this.lastGuess != null) {
            double dx = dug.getX() - this.lastGuess.getX();
            double dz = dug.getZ() - this.lastGuess.getZ();
            errorDistance = Math.sqrt(dx * dx + dz * dz);
            System.out.println("[Diana] Guess error: " + errorDistance + " blocks (2D)");
        }

        // Save data collection entry
        Map<String, Object> entry = new HashMap<>();
        entry.put("timestamp", System.currentTimeMillis());
        entry.put("actualBurrow", blockPosToMap(dug));
        entry.put("guessBurrow", this.lastGuess != null ? blockPosToMap(this.lastGuess) : null);
        entry.put("confidence", this.lastConfidence);
        entry.put("errorDistance2D", errorDistance);
        entry.put("range", this.range != null ? Map.of("min", this.range.min(), "max", this.range.max()) : null);

        if (ray != null) {
            entry.put("rayOrigin", vec3dToMap(ray.origin()));
            entry.put("rayDir", vec3dToMap(ray.dir()));
        }

        List<Map<String, Double>> particles = new ArrayList<>();
        for (Vec3d p : this.dust) {
            particles.add(vec3dToMap(p));
        }
        entry.put("dustParticles", particles);
        entry.put("dustCount", this.dust.size());

        saveDataEntry(entry);
    }

    public void debugInjectSyntheticArrow(Vec3d origin, Vec3d dirIgnored) {
        this.beginAttempt();
        this.range = new IntRange(281, 600);
        Vec3d dir = new Vec3d(1.0, 0.0, 0.0);
        for (int i = 0; i < 20; ++i) {
            Vec3d p = origin.add(dir.multiply((double) i * 0.05));
            this.dust.add(p);
        }
        this.lastDustMs = System.currentTimeMillis();

        double maxStep = 0.0;
        for (int i = 1; i < this.dust.size(); ++i) {
            maxStep = Math.max(maxStep, this.dust.get(i).distanceTo(this.dust.get(i - 1)));
        }
        Ray ray = this.detector.tryDetectRay(this.dust);
        System.out.println("[Diana][dbg] maxConsecutiveStep=" + maxStep);
        System.out.println("[Diana][dbg] dustSize=" + this.dust.size() + " synthetic ray=" + ray);

        if (ray == null) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            System.out.println("[Diana][dbg] world is null");
            return;
        }
        List<Candidate> candidates = this.scanner.scan(mc.world, ray, this.range);
        System.out.println("[Diana][dbg] candidates=" + candidates.size());
        if (candidates.isEmpty()) {
            return;
        }
        List<Candidate> ranked = this.ranker.rank(ray, this.range, candidates);
        BlockPos guess = ranked.get(0).pos;
        double conf = this.ranker.confidence(ranked);
        System.out.println("[Diana][dbg] guess=" + guess + " conf=" + conf
                + " bestScore=" + ranked.get(0).score
                + " secondScore=" + (ranked.size() > 1 ? ranked.get(1).score : -1.0));
    }

    private static Map<String, Double> vec3dToMap(Vec3d v) {
        Map<String, Double> m = new HashMap<>();
        m.put("x", v.x);
        m.put("y", v.y);
        m.put("z", v.z);
        return m;
    }

    private static Map<String, Integer> blockPosToMap(BlockPos pos) {
        Map<String, Integer> m = new HashMap<>();
        m.put("x", pos.getX());
        m.put("y", pos.getY());
        m.put("z", pos.getZ());
        return m;
    }

    private static void saveDataEntry(Map<String, Object> entry) {
        try {
            Path dir = FabricLoader.getInstance().getConfigDir().resolve("coadisplay");
            Files.createDirectories(dir);
            Path file = dir.resolve("diana_data.jsonl");
            String json = GSON.toJson(entry);
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(json);
                w.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Diana] Failed to save data entry: " + e.getMessage());
        }
    }
}
