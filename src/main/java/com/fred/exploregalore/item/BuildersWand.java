package com.fred.exploregalore.item;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.drawing.CubicBezierPathDrawer;
import com.fred.exploregalore.drawing.LinearPathDrawer;
import com.fred.exploregalore.drawing.PathDrawer;
import lombok.AllArgsConstructor;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@Slf4j
public class BuildersWand extends Item {

    private static final String IS_STARTING_BLOCK_SET_TAG_NAME = "is_starting_block_set";

    private static final String BLOCK_POS_LIST_TAG_NAME = "positions";
    private static final byte BLOCK_POS_LIST_TAG_TYPE = Tag.TAG_COMPOUND;

    @AllArgsConstructor
    private enum Mode {
        // TODO: Move strings to language files.
        LINEAR(LinearPathDrawer.INSTANCE, "Linear"),
        CUBIC_BEZIER(CubicBezierPathDrawer.INSTANCE, "Cubic Bezier");

        public static final String TAG_NAME = "mode";

        public static final Mode DEFAULT_MODE = LINEAR;

        private PathDrawer pathDrawer;
        private final String name;

        private static final Mode[] modes = Mode.values();

        public static Mode fromOrdinal(int ordinal) {
            return modes[ordinal];
        }

        public static int numModes() {
            return modes.length;
        }

        public int numRequiredBlockPos() {
            return pathDrawer.numRequiredConfigurationPos();
        }

        public void drawPath(ServerLevel serverLevel, Block block, BlockPos... configurationPos) {
            pathDrawer.drawPath(serverLevel, block, configurationPos);
        }


        @Override
        public String toString() {
            return this.name;
        }
    }

    public BuildersWand() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
    }

    public static void toggleBuildingMode(ServerPlayer player) {
        val heldItem = player.getMainHandItem();

        if (!heldItem.is(ExploreGaloreItems.BUILDERS_WAND.get())) {
            return;
        }

        val tag = heldItem.getOrCreateTag();

        // Checking there is no mode set -> set the default mode.
        if (!tag.contains(Mode.TAG_NAME)) {
            tag.putInt(Mode.TAG_NAME, Mode.DEFAULT_MODE.ordinal());
        }

        // Simple modulus cycling to get the next mode
        int nextMode = (tag.getInt(Mode.TAG_NAME) + 1) % Mode.numModes();
        tag.putInt(Mode.TAG_NAME, nextMode);

        // TODO: Put this into the en_us language file.
        // 'true' as second argument shows a pop-up message; 'false' shows in chat.
        player.displayClientMessage(new TextComponent("Switched drawing mode to " + Mode.fromOrdinal(nextMode)), true);

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

        val drawingMode = Mode.fromOrdinal(wandTag.getInt(Mode.TAG_NAME));

        // Draw the path if the number of saved blockPos is met
        if (numSavedPositions == drawingMode.numRequiredBlockPos()) {
            BlockPos[] positions = wandTag.getList(BLOCK_POS_LIST_TAG_NAME, BLOCK_POS_LIST_TAG_TYPE)
                    .stream()
                    .map(tag -> NbtUtils.readBlockPos((CompoundTag) tag))
                    .toArray(BlockPos[]::new);


            drawingMode.drawPath((ServerLevel) level, Blocks.LIME_WOOL, positions);

            // Clearing the list of blockPos since we're finished drawing.
            wandTag.put(BLOCK_POS_LIST_TAG_NAME, new ListTag());
        }

        return InteractionResult.SUCCESS;

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        log.debug("use triggered!");
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    private int addNewPosToTag(CompoundTag wandTag,
                               BlockPos newlyClickedPos) {
        // The second integer is for the type of the list (in this case a BlockPos tag is a CompoundTag).
        val listOfBlockPosConfig = wandTag.getList(BLOCK_POS_LIST_TAG_NAME, BLOCK_POS_LIST_TAG_TYPE);
        listOfBlockPosConfig.add(NbtUtils.writeBlockPos(newlyClickedPos));
        wandTag.put(BLOCK_POS_LIST_TAG_NAME, listOfBlockPosConfig);

        ExploreGalore.LOGGER.debug(wandTag.toString());

        return listOfBlockPosConfig.size();
    }

/*    private CompoundTag addNewPosToTag(CompoundTag wandTag,
                                       ServerLevel serverLevel,
                                       BlockPos newlyClickedPos) {
        return Optional.of(wandTag)
                // We check if the tag has the starting block set ...
                .filter(compoundTag -> compoundTag.getBoolean(IS_STARTING_BLOCK_SET_TAG_NAME))
                // ... if so, we take the most recently clicked on position, and use it to draw a line
                .map(compoundTag -> {
                    val posFrom = CompoundTagUtils.getBlockPosFromCompoundTag(compoundTag);
                    val posTo = newlyClickedPos;
                    log.debug("Drawing line from {} to {}", posFrom, posTo);
                    LinearPathDrawer.INSTANCE.drawPath(serverLevel, Blocks.ACACIA_WOOD, posFrom, posTo);
                    // We're done drawing the line, reset the state of the tag for the next sequence
                    // To try to be as functional as possible, we don't mutate the old tag; we create a new one.
                    return new CompoundTag();
                })
                // ... if not, then the first block has not been set, and we create a tag with the just-clicked blockPos
                .orElse(CompoundTagUtils.createBlockPosCompoundTag(IS_STARTING_BLOCK_SET_TAG_NAME, newlyClickedPos));
    }*/
}
