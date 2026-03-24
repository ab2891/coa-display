package com.ab2891.coadisplay.diana.guess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class HeuristicRanker implements CandidateRanker {
    @Override
    public List<Candidate> rank(Ray ray, IntRange range, List<Candidate> candidates) {
        candidates.sort((a, b) -> {
            if (a.resolved != b.resolved) {
                return a.resolved ? -1 : 1;
            }
            return Double.compare(a.score, b.score);
        });
        return candidates;
    }

    @Override
    public double confidence(List<Candidate> ranked) {
        if (ranked.size() < 2) {
            return ranked.isEmpty() ? 0.0 : 1.0;
        }
        double a = ranked.get(0).score;
        double b = ranked.get(1).score;
        return (b - a) / (b + 1.0E-6);
    }
}
