package com.ab2891.coadisplay.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

@Environment(EnvType.CLIENT)
public final class ClientScheduler {
    private final Deque<Task> tasks = new ArrayDeque<>();

    public void schedule(long runAtMs, Runnable action) {
        this.tasks.addLast(new Task(runAtMs, action));
    }

    public void tick(MinecraftClient client) {
        if (this.tasks.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Task> it = this.tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (now < t.runAtMs) continue;
            it.remove();
            t.action.run();
        }
    }

    @Environment(EnvType.CLIENT)
    private static final class Task {
        final long runAtMs;
        final Runnable action;

        Task(long runAtMs, Runnable action) {
            this.runAtMs = runAtMs;
            this.action = action;
        }
    }
}
