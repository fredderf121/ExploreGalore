package com.fred.exploregalore.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class BuildersWand extends Item {
    public BuildersWand(String name) {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
    }
}
