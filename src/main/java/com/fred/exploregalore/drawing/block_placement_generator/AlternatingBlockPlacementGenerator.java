package com.fred.exploregalore.drawing.block_placement_generator;


import java.util.ArrayList;
import java.util.List;

/**
 * Cycles between several placement generators when calling {@link #getNextPlacements()}.
 */
public class AlternatingBlockPlacementGenerator implements BlockPlacementGenerator {

    private int count;

    private final List<BlockPlacementContext> alternatingContexts;
    private final int numAlternatingSequences;

    public AlternatingBlockPlacementGenerator(List<BlockPlacementContext> alternatingContexts) {
        if (alternatingContexts.size() == 0) {
            throw new IllegalArgumentException("There must be at least one ConstantBlockPlacementGenerator provided!");
        }

        this.alternatingContexts = alternatingContexts;
        this.numAlternatingSequences = alternatingContexts.size();
        this.count = 0;
    }


    @Override
    public void reset() {
        this.count = 0;
    }


    @Override
    public List<BlockPlacementContext> getNextPlacements() {
        // Cycle through the placement contexts
        return List.of(alternatingContexts.get(count++ % numAlternatingSequences));
    }


}
