package com.fred.exploregalore.item.builderswand;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.drawing.block_placement_context.BlockPlacements;
import com.fred.exploregalore.item.ExploreGaloreItems;
import com.fred.exploregalore.utils.CompoundTagUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Slf4j
public class BuildersWand extends Item {

    private static final String BLOCK_POS_LIST_TAG_NAME = "positions";
    private static final byte BLOCK_POS_LIST_TAG_TYPE = Tag.TAG_COMPOUND;

    public BuildersWand() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
    }

    public static void toggleBuildingMode(ServerPlayer player) {
        val heldItem = player.getMainHandItem();

        if (!heldItem.is(ExploreGaloreItems.BUILDERS_WAND.get())) {
            return;
        }

        val wandTag = heldItem.getOrCreateTag();

        // Checking there is no mode set -> set the default mode.
        val currentMode = getVoxelSequenceModeOrSetDefault(wandTag);

        // Simple modulus cycling to get the next mode
        int nextMode = (currentMode.ordinal() + 1) % VoxelSequenceMode.numModes();
        wandTag.putInt(VoxelSequenceMode.TAG_NAME, nextMode);

        // Clearing the previous list of BlockPos.
        clearBlockPosList(wandTag);

        // TODO: Put this into the en_us language file.
        // 'true' as second argument shows a pop-up message; 'false' shows in chat.
        player.displayClientMessage(new TextComponent("Switched drawing mode to " + VoxelSequenceMode.fromOrdinal(nextMode)), true);

    }

    /**
     * Called when the user right-clicks the wand at a targeted block. (The black wire-frame targeted cut-out is visible).
     * <p>
     * Checks if the is_starting_block_set tag is true. If so, draws a
     * line from the starting block position to the {@link BlockPlaceContext#getClickedPos()} location.
     */
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();


        if (level.isClientSide() || player == null) return InteractionResult.SUCCESS;

        // We want to set the position of the block beside/on-top-of the right-clicked block.
        val itemStack = useOnContext.getItemInHand();
        val wandTag = itemStack.getOrCreateTag();

        int numSavedPositions = addNewPosToTag(wandTag, new BlockPlaceContext(useOnContext).getClickedPos());

        val voxelSequenceMode = getVoxelSequenceModeOrSetDefault(wandTag);
        val blockPlacementMode = getBlockPlacementModeOrSetDefault(wandTag);

        // Draw the path if the number of saved blockPos is met
        if (numSavedPositions == voxelSequenceMode.numRequiredBlockPos()) {
            BlockPos[] configPositions = wandTag.getList(BLOCK_POS_LIST_TAG_NAME, BLOCK_POS_LIST_TAG_TYPE)
                    .stream()
                    .map(tag -> NbtUtils.readBlockPos((CompoundTag) tag))
                    .toArray(BlockPos[]::new);

            // TODO: Code smell with resetting a static final variable!
            BlockPlacements.UMIBO_GAMING_FENCE_DESIGN_2.reset();
            voxelSequenceMode.createSequenceWith(configPositions)
                            .forEach(basisPosition -> {
                                BlockPlacements.placeBlocksAroundBasis((ServerLevel) level, new BlockPos(basisPosition), BlockPlacements.UMIBO_GAMING_FENCE_DESIGN_2);
                            });

            // Clearing the list of blockPos since we're finished drawing.
            clearBlockPosList(wandTag);
        }

        return InteractionResult.SUCCESS;

    }

    // TODO: Add functionality for right-clicking in the air (like an angel block)
    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        log.debug("use triggered!");
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    private static int addNewPosToTag(CompoundTag wandTag,
                                      BlockPos newlyClickedPos) {
        // The second integer is for the type of the list (in this case a BlockPos tag is a CompoundTag).
        val listOfBlockPosConfig = wandTag.getList(BLOCK_POS_LIST_TAG_NAME, BLOCK_POS_LIST_TAG_TYPE);
        listOfBlockPosConfig.add(NbtUtils.writeBlockPos(newlyClickedPos));
        wandTag.put(BLOCK_POS_LIST_TAG_NAME, listOfBlockPosConfig);

        ExploreGalore.LOGGER.debug(wandTag.toString());

        return listOfBlockPosConfig.size();
    }

    private static VoxelSequenceMode getVoxelSequenceModeOrSetDefault(CompoundTag wandTag) {
        return VoxelSequenceMode.fromOrdinal(CompoundTagUtils.getIntOrSetDefault(
                wandTag,
                VoxelSequenceMode.TAG_NAME,
                VoxelSequenceMode.DEFAULT_MODE.ordinal()));
    }

    private static BlockPlacementStrategyMode getBlockPlacementModeOrSetDefault(CompoundTag wandTag) {
        return BlockPlacementStrategyMode.fromOrdinal(CompoundTagUtils.getIntOrSetDefault(
                wandTag,
                BlockPlacementStrategyMode.TAG_NAME,
                BlockPlacementStrategyMode.DEFAULT_MODE.ordinal()
        ));

    }

    private static void clearBlockPosList(CompoundTag wandTag) {
        wandTag.put(BLOCK_POS_LIST_TAG_NAME, new ListTag());
    }


}
