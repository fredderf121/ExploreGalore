package com.fred.exploregalore.commands;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.drawing.LinearPathDrawer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import javax.annotation.Nullable;
import java.util.function.Predicate;


public class DrawBlockPathCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.drawblockpath.failed"));


    /**
     * Some definitions:
     *
     * <p> {@link CommandDispatcher} - Used here for registering our command. It also controls
     * the parsing and executing of commands. </p>
     *
     * <p> {@link CommandSourceStack} - Not too sure; the CommandDispatcher class indicates
     * that it provides info about the source/executor of the command. </p>
     * <br>
     * Below are the following steps that we perform:
     *
     * <ol>
     *     <li>
     *         Register our command via {@link CommandDispatcher#register}. We pass to it
     *         a {@link LiteralArgumentBuilder<CommandSourceStack>} builder object.
     *     </li>
     *     <li>
     *         The builder starts with the first word in our command, "exploregalore". This
     *         is done using {@link LiteralArgumentBuilder#literal(String)}.
     *     </li>
     *     <li>
     *         We continue building our command using {@code .then()}. As the name suggests,
     *         our command then consists of the string literal "drawblockpath" (i.e., the
     *         name of the command).
     *         <ul>
     *             <li>
     *                 Before specifying the next part of the command, we also put a specifier
     *                 on <i>who</i> can use the command using {@code .requires()}. In this
     *                 case, it specifies players that have a certain command execution level.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         Our last parts of the command are two arguments to the command "drawblockpath"
     *          - two {@link BlockPos}. Minecraft provides a whole bunch of pre-formatted arguments,
     *         like blockpos, integers, dimensions, etc.
     *     </li>
     *     <li>
     *         These are all the arguments we want, so we specify what should happen if
     *         the user correctly types all these arguments in using {@code .executes()}.
     *         We provide an implementation of the functional interface {@link Command},
     *         where we call our own method, {@link LinearPathDrawer#drawBlockPath}.
     *         The functional interface provides a {@link CommandContext} as a parameter.
     *         We use that to retrieve values provided in the command (like blockpos).
     *     </li>
     * </ol>
     *
     * @param dispatcher Used to register our command to Minecraft.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(ExploreGalore.MOD_ID)
                        .then(Commands.literal("drawblockpath")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS)) // Includes gamerule, setblock, gamemode
                                .then(Commands.argument("block", BlockStateArgument.block())
                                        .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                                .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                                                        .executes(context -> LinearPathDrawer.drawBlockPath(
                                                                context.getSource().getLevel(),
                                                                BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                BlockPosArgument.getSpawnablePos(context, "startPos"),
                                                                BlockPosArgument.getSpawnablePos(context, "endPos")))
                                                )
                                        )
                                ))
        );

        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(ExploreGalore.MOD_ID)
                        .then(Commands.literal("drawbezierpath")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("block", BlockStateArgument.block())
                                        .then(Commands.argument("P0", BlockPosArgument.blockPos())
                                                .then(Commands.argument("P1", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("P2", BlockPosArgument.blockPos())
                                                                .then(Commands.argument("P3", BlockPosArgument.blockPos())
                                                                        .executes(context ->
                                                                                LinearPathDrawer.drawCubicBezierBlockPath(
                                                                                        context.getSource().getLevel(),
                                                                                        BlockStateArgument.getBlock(context, "block").getState().getBlock(),
                                                                                        BlockPosArgument.getSpawnablePos(context, "P0"),
                                                                                        BlockPosArgument.getSpawnablePos(context, "P1"),
                                                                                        BlockPosArgument.getSpawnablePos(context, "P2"),
                                                                                        BlockPosArgument.getSpawnablePos(context, "P3")))
                                                                )))))

                        ));

    }


    //

    /**
     * <p>
     * Below is the code from SetBlockCommand.java in the Minecraft Source Code.
     * </p>
     * <p>
     * I have annotated it so to help me understand what needs to happen upon attempting
     * to replace a block.
     * </p>
     *
     * @param commandSourceStack    Contains information on the command caller, and the world itself.
     * @param pos                   Position of the block we attempt to replace or destroy.
     * @param blockInput            The type of block that we want to place in the world.
     * @param mode                  Enum that dictates whether we want to replace or destroy (note that the
     *                              argument "keep" uses "replace" enum, and then the predicate below dictates
     *                              that it should only replace if the existing block is air)
     * @param blockInWorldPredicate Predicate that is used to test the existing block. I.e.,
     *                              Use this to test whether or not we want to actually replace/destroy
     *                              the block. Note: {@code @Nullable} is passed if we want
     *                              to "replace" or "destroy". Only "keep" has a predicate.
     * @return A number to indicate if the command executed successfully. 1 is successful.
     * @throws CommandSyntaxException The command arguments had the correct formatting, and
     *                                thus this method was able to be called, but the arguments
     *                                were <i>logically</i> incorrect (like BlockPos is not
     *                                inside the world, such as y < 64)
     */
    private static int setBlock(CommandSourceStack commandSourceStack, BlockPos pos, BlockInput blockInput, SetBlockCommand.Mode mode, @Nullable Predicate<BlockInWorld> blockInWorldPredicate) throws CommandSyntaxException {
        ServerLevel serverlevel = commandSourceStack.getLevel();

        // Used for "keep" argument, in which we are using the predicate to test if the target
        // BlockPos is empty (air)
        if (blockInWorldPredicate != null && !blockInWorldPredicate.test(new BlockInWorld(serverlevel, pos, true))) {
            // In the original code, this has actual error messages - I've omitted it because it requires additional class imports.
            throw new CommandSyntaxException(null, null);
        }
        // Used for "replace" or "destroy"
        else {
            // Essentially signals if we need to do anything. As you will see below,
            // this occurs if:
            // - EITHER (including BOTH) the existing block and to-be-placed block is not Air
            // - The existing block is a BlockEntity
            boolean flag;
            // "destroy"
            // This section solely tries to destroy the block at blockPos.
            if (mode == SetBlockCommand.Mode.DESTROY) {
                // Destroys the block, and if it is a BlockEntity with inventory, clear the
                // inventory as well.
                serverlevel.destroyBlock(pos, true);
                // flag = (The block we WANT to place is NOT air) OR (The existing block is NOT air)
                // I.e, FALSE if BOTH the existing block and to-be-placed block is both Air
                flag = !blockInput.getState().isAir() || !serverlevel.getBlockState(pos).isAir();
            }
            // "replace"
            else {
                // Handles the condition for which the block to-be-replaced is a BlockEntity.
                BlockEntity blockentity = serverlevel.getBlockEntity(pos);
                // If it is a BlockEntity, we clear its inventory.
                Clearable.tryClear(blockentity);
                flag = true;
            }

            // Attempt to place the block. Not too sure what UPDATE_CLIENTS entails.
            if (flag && !blockInput.place(serverlevel, pos, Block.UPDATE_CLIENTS)) {
                throw new CommandSyntaxException(null, null);
            } else {
                // Reaching here means the placement is successful.
                // Notify the server and make appropriate changes (like notifying the surrounding
                // six blocks)
                serverlevel.blockUpdated(pos, blockInput.getState().getBlock());
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.setblock.success", pos.getX(), pos.getY(), pos.getZ()), true);
                return 1;
            }
        }
    }


}
