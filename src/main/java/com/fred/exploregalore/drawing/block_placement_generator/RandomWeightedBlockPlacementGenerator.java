package com.fred.exploregalore.drawing.block_placement_generator;


import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

public class RandomWeightedBlockPlacementGenerator implements BlockPlacementGenerator {

    private final EnumeratedDistribution<BlockPlacementContext> randomPlacementContextPool;
    private final BlockPlacementContext generatedContext;

    /**
     * For immutability and repeatability, we generate the {@link BlockPlacementContext} upon creation of this object.
     */
    public RandomWeightedBlockPlacementGenerator(EnumeratedDistribution<BlockPlacementContext> placementContexts) {
        this.randomPlacementContextPool = placementContexts;
        this.generatedContext = placementContexts.sample();
    }

    // TODO: Find better organization, as this *always* only returns one block at a time
    @Override
    public List<BlockPlacementContext> getPlacements() {
        return List.of(generatedContext);
    }

    @Override
    public BlockPlacementGenerator update() {
        return new RandomWeightedBlockPlacementGenerator(randomPlacementContextPool);
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
