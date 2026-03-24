package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class Candidate {
    public BlockPos pos;
    public final boolean resolved;
    public final double score;
    public final double distFromOrigin;

    public Candidate(BlockPos pos, boolean resolved, double score, double distFromOrigin) {
        this.pos = pos;
        this.resolved = resolved;
        this.score = score;
        this.distFromOrigin = distFromOrigin;
    }
}
