package fred.exploregalore.core;

import fred.exploregalore.ExploreGalore;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemList {

    // Items
    public static final Item EXPLORERS_TOOL;
    public static final Item MAGIC_WAND;
    public static final Item LIVING_LILY_STEM;

    // Block Items
    public static final Item MOON_STONE;

    static {

        // Items
        EXPLORERS_TOOL = registerItem(new Item(new Item.Settings().group(ItemGroup.MISC).fireproof()), "explorers_tool");
        MAGIC_WAND = registerItem(new Item(new Item.Settings().group(ItemGroup.COMBAT).maxCount(1)), "magic_wand");

        // Block Items
        MOON_STONE = registerItem(new BlockItem(BlockList.MOON_STONE, new Item.Settings().group(ItemGroup.MISC)), Registry.BLOCK.getId(BlockList.MOON_STONE));
        LIVING_LILY_STEM = registerItem(new BlockItem(BlockList.LIVING_LILY_STEM, new Item.Settings().group(ItemGroup.MISC)), Registry.BLOCK.getId(BlockList.LIVING_LILY_STEM));

    }

    private static <T extends Item> T registerItem(T item, String registryName) {

        return registerItem(item, new Identifier(ExploreGalore.MOD_ID, registryName));
    }

    private static <T extends Item> T registerItem(T item, Identifier id) {
        return Registry.register(Registry.ITEM, id, item);
    }

    // Used to load the class - resulting in the execution of the static block.
    // The method itself does nothing - it just forces the jvm to initalize the class.
    public static void initalizeAndRegister() {}
}
