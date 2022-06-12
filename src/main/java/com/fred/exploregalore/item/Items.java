package com.fred.exploregalore.item;

import com.fred.exploregalore.ExploreGalore;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {

    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ExploreGalore.MOD_ID);

    public static final RegistryObject<Item> BUILDERS_WAND = ITEM_REGISTRY.register("builders_wand", () -> new BuildersWand("builders_wand"));

}
