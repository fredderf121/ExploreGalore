package com.fred.exploregalore.drawing.block_placement_generator;


import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RandomWeightedBlockPlacementGenerator implements BlockPlacementGenerator {
    private final EnumeratedDistribution<BlockPlacementContext> randomPlacementContextPool;

    public RandomWeightedBlockPlacementGenerator(EnumeratedDistribution<BlockPlacementContext> placementContexts) {
        this.randomPlacementContextPool = placementContexts;
    }

    // TODO: Find better organization, as this *always* only returns one block at a time
    @Override
    public List<BlockPlacementContext> getNextPlacements() {
        return List.of(randomPlacementContextPool.sample());
    }




    /**
     * Does nothing since the generator is random.
     */
    @Override
    public void reset() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Pair<BlockPlacementContext, Double>> blockPlacementPool;
        private Builder() {
            blockPlacementPool = new ArrayList<>();
        }

        public Builder add(BlockPlacementContext context, double weighting) {
            blockPlacementPool.add(Pair.create(context, weighting));
            return this;
        }

        public RandomWeightedBlockPlacementGenerator build() {
            return new RandomWeightedBlockPlacementGenerator(new EnumeratedDistribution<>(blockPlacementPool));
        }


    }
}
