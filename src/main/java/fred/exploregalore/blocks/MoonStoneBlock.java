package fred.exploregalore.blocks;

import fred.exploregalore.lib.DiscreteCircle;
import fred.exploregalore.lib.EntityPrevPosAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * The moonstone block. <br>
 * Its ai is as follows: <br>
 * <ol>
 *     <li>Detect when an entity steps on a Moon Stone Block.</li>
 *     <li>Light up the block.</li>
 *     <li>In outward concentric circles, schedule for rings of Moon Stone blocks to light up surrounding the block that
 *     was stepped on.</li>
 *     <li>After X seconds, check for if there is a <i>moving</i> entity still on the block. If so, keep it lit and follow
 *     step (3) for lighting up the surrounding blocks.
 *     If there is no moving entity on the block, make the block dark again.</li>
 *
 * </ol>
 */
public class MoonStoneBlock extends Block {

    /**
     * Bounding box of a block 1 above the desired block - used to detect what entities are standing on a block.
     */
    private static final Box ONE_BLOCK_ABOVE_BOX;
    /**
     * Error for comparing doubles used in movement.
     * Ex: To detect if an entity has moved, we can compare prevX with getX() to see if they are the same. Due to some
     * discreptancies in movement mechanics, we make the error quite large.
     */
    public static final double ERROR_EPSILON;

    /**
     * <ol start = "1">
     *     <li>Define the property of the block & instantiate it (use one that's already created for us)</li>
     * </ol>
     */
    public static final BooleanProperty LIT;

    /**
     * The amount of time a MoonStoneBlock should stay lit for.
     */
    public static final int LIT_TIME;

    public static final int MIN_LIGHT_RADIUS;

    static {
        ONE_BLOCK_ABOVE_BOX = new Box(0, 1, 0, 1, 2, 1);
        ERROR_EPSILON = 0.02D;
        LIT = Properties.LIT;
        LIT_TIME = 40;
        MIN_LIGHT_RADIUS = 1;
    }


    /**
     * <ol start = "2">
     *     <li>Register the property; override the method to add the property to the list of states</li>
     * </ol>
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(LIT);
    }


    /**
     * <ol start = "3">
     *     <li>Setting the default state LIT of the block in the constructor. <br>
     *       Take the default state and add to it the LIT default state -> .with()</li>
     * </ol>
     */
    public MoonStoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false));

    }


    /**
     * <ol start = "4">
     * <li>
     *   a. Update the state of the MoonStoneBlock if an entity is stepping on it. Light up the <i>singular</i> block
     *   that has been stepped on.
     * </li>
     * </ol>
     */
    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {


        if ((!world.isClient) && entity instanceof LivingEntity) {

            BlockState steppedOnBlockState = world.getBlockState(pos);
            boolean isLit = steppedOnBlockState.get(LIT);
            if (!isLit) {

                // Walking on the ground
                if (isEntityWalkingServer(entity)) {
                    // maxDistFromCenter is one - just the singular block
                    lightBlocksInRadiatingCircles(steppedOnBlockState, world, pos, MoonStoneBlock.MIN_LIGHT_RADIUS);
                }
            }

            // We use some custom prevX/Y/Z variables (via mixin) to save the previous pos of entities - this is due
            // to the player entity's prevX/Y/Z variables not working serverside
            ((EntityPrevPosAccess) entity).savePrevPos();
        }
        super.onSteppedOn(world, pos, entity);
    }
    /*
     * 1. On block stepped on, check if entity is moving.
     * 2. If moving, light up the block, and cause surrounding blocks to light up.
     * 3. After X ticks, extinguish the block. However, if there is further movement at the original block, keep it lit
     *    and reschedule the extinguishing process for X ticks.
     *
     * */


    /**
     * <ol start = "4">
     * <li>
     *   b. Update the state of the MoonStoneBlock if an entity has landed on it. Light up the landed-on block as well
     *   as surrounding blocks in concentric/radiating circles. The number of blocks lit up / the radius of blocks from
     *   the center to light up is determined by the fall distance. Larger fall distance means a greater radius.
     * </li>
     * </ol>
     */
    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {


        if ((!world.isClient) && entity instanceof LivingEntity) {

            BlockState steppedOnBlockState = world.getBlockState(pos);

            //if (!steppedOnBlockState.get(LIT)) {
                int radiatingCircleRadius = (distance * 0.5F) > (DiscreteCircle.MAX_RADIUS - 1) ? DiscreteCircle.MAX_RADIUS : (int) (distance * 0.5F) + 2;
                lightBlocksInRadiatingCircles(steppedOnBlockState, world, pos, radiatingCircleRadius);
          //  }

            ((EntityPrevPosAccess) entity).savePrevPos();
        }
        super.onLandedUpon(world, pos, entity, distance);
    }



    /**
     * Lights up the steppedOnBlock and its surrounding blocks in a radiating circle effect.
     *
     * @param steppedOnBlockState block that was stepped on - that triggers the 'blooming' effect
     */
    private void lightBlocksInRadiatingCircles(BlockState steppedOnBlockState, World world, BlockPos centralBlockPos, int maxDistFromCenter) {
        /* Lighting up the initial block */
        // Light the block ...
        light(steppedOnBlockState, world, centralBlockPos);
        // ... And then set for the block to go out after X ticks
        world.getBlockTickScheduler().schedule(centralBlockPos, this, MoonStoneBlock.LIT_TIME);

        /* Lighting up the surrounding blocks, one 'ring' at a time with delay for visual effect. */

        int delay = 1;

        for (int radius = MoonStoneBlock.MIN_LIGHT_RADIUS; radius <= maxDistFromCenter; radius++) {
            for (int[] point : DiscreteCircle.POINTS[radius - 1]) {
                world.getBlockTickScheduler().schedule(centralBlockPos.add(point[0], 0, point[1]), this, radius * delay);

            }
        }
    }


    /**
     * <ol start="5">
     *     <li>
     *         The scheduled tick for the block to extinguish its light.<br>
     *         Before we extinguish its light, we check for if there is an entity on the block, and if the entity is moving.
     *     </li>
     * </ol>
     */
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

        if (!world.isClient()) {
            if (state.get(LIT)) {
                boolean hasWalkingEntitiesOnTop = !(getWalkingEntitiesOnTop(world, pos).isEmpty());

                if (hasWalkingEntitiesOnTop) {
                    world.getBlockTickScheduler().schedule(pos, this, MoonStoneBlock.LIT_TIME);
                } else {
                    extinguish(state, world, pos);
                }
            } else {
                light(state, world, pos);
                world.getBlockTickScheduler().schedule(pos, this, MoonStoneBlock.LIT_TIME);
            }


        }

    }


    /**
     * Sets the LIT state of the block to true. When rendered, the block will light up.
     */
    private void light(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, true));
    }

    private void extinguish(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, false));
    }

    /**
     * Gets all the entities on top of the current (MoonStone) block, and filters only the ones that are walking.
     *
     * @return a list of all the entities that are walking on top of the block.
     */
    private List<Entity> getWalkingEntitiesOnTop(World world, BlockPos pos) {
        List<LivingEntity> entitiesInBox = world.getNonSpectatingEntities(LivingEntity.class, ONE_BLOCK_ABOVE_BOX.offset(pos));
        return entitiesInBox.stream()
                .filter(this::isEntityWalkingServer)
                .collect(Collectors.toList());
    }

    /**
     * Checks if an entity is walking - that is, if its moving in the x/z direction and if it's on the ground.<br>
     * We require the use of mixins as the server does not store the previous velocities of the player entity.
     *
     * @return true if the entity has a non-zero velocity
     */
    private boolean isEntityWalkingServer(Entity entity) {


        boolean isOnGround = entity.isOnGround();

        boolean isXVelocityZero = !(Math.abs(((EntityPrevPosAccess) entity).getPrevXServer() - entity.getX()) > ERROR_EPSILON);
        boolean isZVelocityZero = !(Math.abs(((EntityPrevPosAccess) entity).getPrevZServer() - entity.getZ()) > ERROR_EPSILON);
        boolean isFalling = entity.fallDistance > ERROR_EPSILON;

        // Entity is on ground AND y-velocity is 0 AND X/Z velocities are non-zero
        // OR, Entity on ground AND y-velocity is non-zero AND (X AND Z velocities are zero)
        boolean isWalking = !isFalling && isOnGround && (!isXVelocityZero || !isZVelocityZero);


        return isWalking;
    }


    /**
     * Method used for the luminance property
     *
     * @see fred.exploregalore.core.BlockList BlockList.MOON_STONE Instantiation
     */
    public static boolean isLit(BlockState blockState) {
        return blockState.get(LIT);
    }
}