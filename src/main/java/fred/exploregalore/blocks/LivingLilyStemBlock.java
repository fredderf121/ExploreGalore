package fred.exploregalore.blocks;

import fred.exploregalore.blocks.enums.LilyStemType;
import fred.exploregalore.core.BlockList;
import fred.exploregalore.state.property.ExploreGaloreProperties;
import fred.exploregalore.tag.ExploreGaloreBlockTags;
import fred.exploregalore.util.shape.RotatableVoxelShapeGroup;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


/**
 * A plant stem block that is part of a multi-part structure, the Living Lily Stem.<br>
 * This stem must have its base in water, growing on dirt. <br>
 * The stem block has several different appearances depending on where it's placed:
 * <ol>
 *     <li>
 *         If it is directly touching the ground, then it takes the shape of the <u>base shape</u>.
 *     </li>
 *     <li>
 *         If it is not attached to anything and is not the base shape, then it takes the shape of the <u>default shape</u>.
 *     </li>
 *     <li>
 *         If it is attached to a Living Lily Pad (which will never generate at the base or top of the stem, then it
 *         takes the shape of the <u>attached middle shape</u>.
 *     </li>
 *     <li>
 *         If it is attached to a Living Lily Flower (which will always generate at the topmost stem), it will take the shape
 *         of the <u>attached top shape</u>.
 *     </li>
 * </ol>
 */
public class LivingLilyStemBlock extends PlantBlock implements Waterloggable {

    // Properties
    public static final DirectionProperty FACING;
    public static final EnumProperty<LilyStemType> LILY_STEM_TYPE;
    public static final BooleanProperty WATERLOGGED;

    // VoxelShapes
    public static final RotatableVoxelShapeGroup BASE_SHAPE;
    public static final RotatableVoxelShapeGroup DEFAULT_SHAPE;
    public static final RotatableVoxelShapeGroup ATTACHED_MIDDLE_SHAPE;
    public static final RotatableVoxelShapeGroup ATTACHED_TOP_SHAPE;


    public LivingLilyStemBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LILY_STEM_TYPE, LilyStemType.DEFAULT)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LILY_STEM_TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(LILY_STEM_TYPE)) {
            case BASE -> BASE_SHAPE.getShapeFromOrientation(state.get(FACING));
            case DEFAULT -> DEFAULT_SHAPE.getShapeFromOrientation(state.get(FACING));
            case ATTACHED_MIDDLE -> ATTACHED_MIDDLE_SHAPE.getShapeFromOrientation(state.get(FACING));
            case ATTACHED_TOP -> ATTACHED_TOP_SHAPE.getShapeFromOrientation(state.get(FACING));
        };
    }

    /**
     * Returns what blockState the block should be when placed.<br>
     * In this case, the blockState depends on whether or not there is water, as well as the direction the player is
     * facing.
     */
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        /*
        Why I check for nullity: I'm learning from SmallDripLeafBlock, where they check for nullity. This is because
        SmallDripLeafBlock inherits TallPlantBlock. There, null is returned if the block to be placed is at the
        height limit, or if the above block can't be replaced.
         */
        if (blockState != null) {
            World world = ctx.getWorld();
            BlockPos blockPos = ctx.getBlockPos();

            // Checking if the block will be placed into water
            FluidState fluidState = world.getFluidState(blockPos);
            boolean isInWater = fluidState.getFluid() == Fluids.WATER;

            // Checking if the block is touching the ground; make it base block
            BlockState floorBlockState = world.getBlockState(blockPos.down());
            boolean isTouchingGroundBlock = floorBlockState.isIn(ExploreGaloreBlockTags.LIVING_LILY_STEM_PLACEABLE);

            return blockState
                    .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                    .with(LILY_STEM_TYPE, isTouchingGroundBlock ? LilyStemType.BASE : LilyStemType.DEFAULT)
                    .with(FACING, ctx.getPlayerFacing().getOpposite());
        }
        return null;
    }

    /**
     * Not too sure what this does???
     * @param state
     * @return
     */
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    /**
     * Dictates whether or not this block can be 'planted' on top of. In this case, we check:
     * <ol>
     *     <li>
     *         The target block is a block from the specified tag list (LIVING_LILY_STEM_PLACEABLE)
     *     </li>
     *     <li>
     *         A source block of water is covering the target block.
     *     </li>
     * </ol>
     *
     * @param floor the target block; the block that the plant is to be placed on top of.
     * @param world a limited world that includes (but is not limited to) blockstates
     * @param pos   the position of the target block (NOT the block on top)
     * @return whether the plant can be placed
     */
    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return (floor.isIn(ExploreGaloreBlockTags.LIVING_LILY_STEM_PLACEABLE) && world.getFluidState(pos.up()).isEqualAndStill(Fluids.WATER))
                || floor.isOf(BlockList.LIVING_LILY_STEM);
    }

    /**
     * Dictates whether a block can be placed in the world.
     * It must meet two conditions:
     * <ol>
     *     <li>
     *         The floor block can be planted on (we check canPlantOnTop)
     *     </li>
     *     <li>
     *         OR, the floor block is another LivingLilyStemBlock.
     *     </li>
     * </ol>
     * @param state ?? not too sure
     * @param pos the position that the block is to be placed in
     * @return whether or not the block can be placed at the specified position.
     */
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos floorBlockPos = pos.down();
        BlockState floorBlockState = world.getBlockState(floorBlockPos);
        return this.canPlantOnTop(floorBlockState, world, pos) || floorBlockState.isOf(BlockList.LIVING_LILY_STEM);
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
        LILY_STEM_TYPE = ExploreGaloreProperties.LILY_STEM_TYPE;
        WATERLOGGED = Properties.WATERLOGGED;

        // Initializing the VoxelShapes. BlockBench is used for obtaining VoxelShapes coordinates.
        BASE_SHAPE = new RotatableVoxelShapeGroup(
                new Box(7, 2, 7, 9, 16, 9),
                new Box(2, 0, 2, 14, 2, 14));
        DEFAULT_SHAPE = new RotatableVoxelShapeGroup(
                new Box(7, 0, 7, 9, 16, 9));
        ATTACHED_MIDDLE_SHAPE = new RotatableVoxelShapeGroup(
                new Box(7, 1, 0, 9, 2, 7),
                new Box(7, 0, 7, 9, 16, 9));
        ATTACHED_TOP_SHAPE = new RotatableVoxelShapeGroup(
                new Box(7, 13, 0, 9, 14, 3),
                new Box(7, 0, 7, 9, 11, 9),
                new Box(7, 10, 5, 9, 12, 7),
                new Box(7, 12, 3, 9, 13, 5));
    }
}
