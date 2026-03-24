package com.ab2891.coadisplay.crown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CrownCache {
    private long coinsConsumed = -1L;

    public long getCoinsConsumed() {
        return this.coinsConsumed;
    }

    public void setCoinsConsumed(long coinsConsumed) {
        this.coinsConsumed = coinsConsumed;
    }
}
