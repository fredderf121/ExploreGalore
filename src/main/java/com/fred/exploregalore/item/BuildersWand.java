package com.fred.exploregalore.item;

import com.fred.exploregalore.drawing.LinearPathDrawer;
import com.fred.exploregalore.network.ExploreGaloreNetwork;
import com.fred.exploregalore.utils.CompoundTagUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

@Slf4j
public class BuildersWand extends Item {

    private static final String IS_STARTING_BLOCK_SET_TAG_NAME = "is_starting_block_set";

    public BuildersWand() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
    }

    public static void toggleBuildingMode(ServerPlayer player) {
        val heldItem = player.getMainHandItem();

        if (heldItem.is(ExploreGaloreItems.BUILDERS_WAND.get())) {
            // 'true' as second argument shows a pop-up message; 'false' shows in chat.
            player.displayClientMessage(new TextComponent("Key was pressed, todo personalized message for switching wand mode!"), true);
        }
    }

    /**
     * Called when the user right-clicks the wand.
     * <p>
     * Checks if the is_starting_block_set tag is true. If so, draws a
     * line from the starting block position to the {@link BlockPlaceContext#getClickedPos()} location.
     *
     * @param useOnContext
     * @return
     */
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {

        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();

        if (world.isClientSide() || player == null) return InteractionResult.SUCCESS;

        // We want to set the position of the block *beside* the right-clicked block.
        val blockPlaceContext = new BlockPlaceContext(useOnContext);

        val itemStack = useOnContext.getItemInHand();

        val firstBlockPlacementTag = Optional
                // Tag can initially be nullable
                .ofNullable(itemStack.getTag())
                // We check if the tag has the starting block set ...
                .filter(compoundTag -> compoundTag.getBoolean(IS_STARTING_BLOCK_SET_TAG_NAME))
                // ... if so, we take the most recently clicked on position, and use it to draw a line
                .map(compoundTag -> {
                    val posFrom = CompoundTagUtils.getBlockPosFromCompoundTag(compoundTag);
                    val posTo = blockPlaceContext.getClickedPos();
                    log.debug("Drawing line from {} to {}", posFrom, posTo);
                    LinearPathDrawer.drawBlockPath((ServerLevel) world, Blocks.ACACIA_WOOD, posFrom, posTo);
                    // We're done drawing the line, reset the state of the tag for the next sequence
                    // To try to be as functional as possible, we don't mutate the old tag; we create a new one.
                    return new CompoundTag();
                })
                // ... if not, then the first block has not been set, and we create a tag with the just-clicked blockPos
                .orElse(CompoundTagUtils.createBlockPosCompoundTag(IS_STARTING_BLOCK_SET_TAG_NAME, blockPlaceContext.getClickedPos()));

        itemStack.setTag(firstBlockPlacementTag);

        return InteractionResult.SUCCESS;

    }


}
