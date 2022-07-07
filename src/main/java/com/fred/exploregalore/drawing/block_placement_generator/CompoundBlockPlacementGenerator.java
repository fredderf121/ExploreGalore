package com.fred.exploregalore.drawing.block_placement_generator;

import java.util.List;

public class CompoundBlockPlacementGenerator implements BlockPlacementGenerator {
    private final List<BlockPlacementGenerator> placementGenerators;

    public CompoundBlockPlacementGenerator(List<BlockPlacementGenerator> generators) {
        this.placementGenerators = generators;
    }

    @Override
    public List<BlockPlacementContext> getPlacements() {
        // TODO: flatMap could be a performance concern?
        return placementGenerators.stream()
                .map(BlockPlacementGenerator::getPlacements)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public BlockPlacementGenerator update() {
        return new CompoundBlockPlacementGenerator(placementGenerators.stream().map(BlockPlacementGenerator::update).toList());
    }


}
