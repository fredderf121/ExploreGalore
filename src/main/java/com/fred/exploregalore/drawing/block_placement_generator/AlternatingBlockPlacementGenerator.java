package com.fred.exploregalore.drawing.block_placement_generator;


import java.util.List;

/**
 * Cycles between several placement generators when calling {@link #getPlacements()}.
 */
public class AlternatingBlockPlacementGenerator implements BlockPlacementGenerator {

    private final int count;

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

    private AlternatingBlockPlacementGenerator(List<BlockPlacementContext> alternatingContexts, int count) {
        this.alternatingContexts = alternatingContexts;
        this.numAlternatingSequences = alternatingContexts.size();
        this.count = count;
    }




    @Override
    public List<BlockPlacementContext> getPlacements() {
        return List.of(alternatingContexts.get(count % numAlternatingSequences));
    }

    @Override
    public BlockPlacementGenerator update() {
        return new AlternatingBlockPlacementGenerator(alternatingContexts, count + 1);
    }


}
