package com.fred.exploregalore.drawing.block_placement_context;

import java.util.List;

public class ConstantBlockPlacementGenerator implements BlockPlacementGenerator {

    private final List<BlockPlacementContext> placementContexts;

    public ConstantBlockPlacementGenerator(List<BlockPlacementContext> placementContexts) {
        this.placementContexts = placementContexts;
    }

    public ConstantBlockPlacementGenerator(BlockPlacementContext... placementContexts) {
        this.placementContexts = List.of(placementContexts);
    }


    @Override
    public List<BlockPlacementContext> getNextPlacements() {
        return placementContexts;
    }
}
