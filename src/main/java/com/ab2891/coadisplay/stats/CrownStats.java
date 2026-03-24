package com.ab2891.coadisplay.stats;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class CrownStats {
    public int crownsCompleted = 0;
    public Map<String, Long> lastCoinsByUUID = new HashMap<>();
}
