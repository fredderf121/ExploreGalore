package com.fred.exploregalore.drawing.block_placement_generator;

import com.fred.exploregalore.drawing.PathDrawer;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface BlockPlacementGenerator {

    default void placeBlocksAroundBasis(ServerLevel serverLevel, BlockPos basisPosition) {
        getNextPlacements()
                .forEach(context -> PathDrawer.tryPlacingBlock(serverLevel, basisPosition.offset(context.relativePos()), context.blockState()));
    }

    /**
     * Returns the next BlockState, and where it should be placed <i>relative to a basis BlockPos</i>.
     *
     * <p>
     * In the case of fairly complex placement contexts, this may have the effect of <i>mutating</i> this
     * object's state, such that the NEXT call of {@link #getNextPlacements()} may return a different value.
     * </p>
     */
    List<BlockPlacementContext> getNextPlacements();

    /**
     * Sets the generator back to its initial state.
     */
    void reset();

    static Builder builder() {
        return new Builder();
    }


    // TODO: Enforce at-least one argument in the var-args methods.
    class Builder {

        private final List<BlockPlacementGenerator> generators;

        private Builder() {
            this.generators = new ArrayList<>();
        }

        public Builder constant(BlockPlacementContext context) {
            generators.add(new ConstantBlockPlacementGenerator(context));
            return this;
        }

        public Builder constant(BlockPlacementContext... contexts) {
            generators.add(new ConstantBlockPlacementGenerator(contexts));
            return this;
        }


        public Builder alternating(BlockPlacementContext... contexts) {
            generators.add(new AlternatingBlockPlacementGenerator(List.of(contexts)));
            return this;
        }

        public Builder alternatingSamePosition(BlockPos relativePos, BlockState... blockStates) {
            generators.add(new AlternatingBlockPlacementGenerator(Arrays.stream(blockStates)
                    .map(blockState -> new BlockPlacementContext(relativePos, blockState))
                    .toList()
            ));
            return this;
        }

        public Builder random(Pair<BlockPlacementContext, Double>... weightedPlacementContexts) {
            generators.add(new RandomWeightedBlockPlacementGenerator(new EnumeratedDistribution<>(List.of(weightedPlacementContexts))));
            return this;
        }

        public Builder randomSamePosition(BlockPos relativePos, Pair<BlockState, Double>... weightedBlockStates) {
            generators.add(new RandomWeightedBlockPlacementGenerator(
                    new EnumeratedDistribution<>(
                            Arrays.stream(weightedBlockStates)
                                    .map(weightedBlockState ->
                                            Pair.create(
                                                    new BlockPlacementContext(relativePos, weightedBlockState.getKey()),
                                                    weightedBlockState.getValue()))
                                    .toList()

                    )
            ));

            return this;
        }


        public BlockPlacementGenerator build() {
            if (generators.isEmpty()) {
                throw new UnsupportedOperationException("At least one builder method must be called!");
            } else if (generators.size() == 1) {
                return generators.get(0);
            } else {
                return new CompoundBlockPlacementGenerator(generators);
            }
        }
    }


}
