package com.fred.exploregalore.item.builderswand;

import com.fred.exploregalore.drawing.block_placement_strategy.BlockPlacementStrategy;
import com.fred.exploregalore.drawing.block_placement_strategy.SingleBlockPlacementStrategy;
import lombok.AllArgsConstructor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

@AllArgsConstructor
public enum BlockPlacementStrategyMode {
    SINGLE(serverLevel -> new SingleBlockPlacementStrategy(serverLevel, Blocks.PINK_WOOL.defaultBlockState()),
            "Single Placement Strategy");

    public static final String TAG_NAME = "placement_strategy";

    public static final BlockPlacementStrategyMode DEFAULT_MODE = SINGLE;


    // Note that the order matters due to Lombok's @AllArgsConstructor.
    private final Function<ServerLevel, BlockPlacementStrategy> factoryGeneratorFunction;
    private final String name;

    private static final BlockPlacementStrategyMode[] MODES = BlockPlacementStrategyMode.values();

    public static BlockPlacementStrategyMode fromOrdinal(int ordinal) {
        return MODES[ordinal];
    }

    public static int numModes() {
        return MODES.length;
    }

    public BlockPlacementStrategy createStrategyForLevel(ServerLevel serverLevel) {
        return this.factoryGeneratorFunction.apply(serverLevel);
    }


    @Override
    public String toString() {
        return this.name;
    }
}
