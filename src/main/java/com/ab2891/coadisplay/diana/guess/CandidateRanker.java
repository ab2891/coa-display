package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface CandidateRanker {
    List<Candidate> rank(Ray ray, IntRange range, List<Candidate> candidates);

    default double confidence(List<Candidate> ranked) {
        return 0.0;
    }
}
