package com.ab2891.coadisplay.crown;

import com.ab2891.coadisplay.stats.CrownStats;
import com.ab2891.coadisplay.stats.StatsManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CrownCompletionTracker {
    private static final long CAP = 1_000_000_000L;

    private CrownCompletionTracker() {
    }

    public static void onCrownCoinsRead(String crownUuid, long coinsNow) {
        CrownStats stats = StatsManager.get();
        long last = stats.lastCoinsByUUID.getOrDefault(crownUuid, -1L);
        if (last != coinsNow) {
            stats.lastCoinsByUUID.put(crownUuid, coinsNow);
            StatsManager.markDirty();
        }
        if (last < 0L) {
            return;
        }
        if (last < CAP && coinsNow >= CAP) {
            ++stats.crownsCompleted;
            StatsManager.save();
        }
    }
}
