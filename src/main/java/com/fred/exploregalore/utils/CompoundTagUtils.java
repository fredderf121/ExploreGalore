package com.fred.exploregalore.utils;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class CompoundTagUtils {
    public static int getIntOrSetDefault(CompoundTag tag, String key, int defaultValue) {
        if (tag.contains(key)) {
            return tag.getInt(key);
        }
        tag.putInt(key, defaultValue);
        return defaultValue;
    }
}
