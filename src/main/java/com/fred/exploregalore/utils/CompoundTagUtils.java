package com.fred.exploregalore.utils;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class CompoundTagUtils {
    public static CompoundTag createBlockPosCompoundTag(String existsTagName, BlockPos pos) {
        val tag = new CompoundTag();
        tag.putBoolean(existsTagName, true);
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    public static BlockPos getBlockPosFromCompoundTag(CompoundTag tag) {
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
}
