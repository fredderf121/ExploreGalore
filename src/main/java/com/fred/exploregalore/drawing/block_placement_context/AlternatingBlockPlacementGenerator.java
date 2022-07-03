package com.fred.exploregalore.drawing.block_placement_context;


import java.util.List;

/**
 * Cycles between several placement generators when calling {@link #getNextPlacements()}.
 */
public class AlternatingBlockPlacementGenerator implements BlockPlacementGenerator {

    private int count;

    private final List<BlockPlacementGenerator> alternatingGenerators;
    private final int numAlternatingSequences;

    public AlternatingBlockPlacementGenerator(BlockPlacementGenerator... alternatingGenerators) {
        this(List.of(alternatingGenerators));
    }

    public AlternatingBlockPlacementGenerator(List<BlockPlacementGenerator> alternatingGenerators) {
        if (alternatingGenerators.size() == 0) {
            throw new IllegalArgumentException("There must be at least one ConstantBlockPlacementGenerator provided!");
        }

        this.alternatingGenerators = alternatingGenerators;
        this.numAlternatingSequences = alternatingGenerators.size();
        this.count = 0;
    }

    @Override
    public void reset() {
        this.count = 0;
        alternatingGenerators.forEach(BlockPlacementGenerator::reset);
    }

    @Override
    public List<BlockPlacementContext> getNextPlacements() {
        // Cycle through the constant generators, and increment count so that the next call of getNextPlacements
        // returns the next sequence of placements.
        return alternatingGenerators.get(count++ % numAlternatingSequences).getNextPlacements();
    }
}
