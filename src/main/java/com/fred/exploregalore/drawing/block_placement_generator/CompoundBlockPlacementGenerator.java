package com.fred.exploregalore.drawing.block_placement_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CompoundBlockPlacementGenerator implements BlockPlacementGenerator {
    private final List<BlockPlacementGenerator> placementGenerators;

    public CompoundBlockPlacementGenerator(List<BlockPlacementGenerator> generators) {
        this.placementGenerators = generators;
    }

    @Override
    public List<BlockPlacementContext> getNextPlacements() {
        // TODO: flatMap could be a performance concern?
        return placementGenerators.stream()
                .map(BlockPlacementGenerator::getNextPlacements)
                .flatMap(List::stream)
                .toList();
    }


    @Override
    public void reset() {
        placementGenerators.forEach(BlockPlacementGenerator::reset);
    }
}
