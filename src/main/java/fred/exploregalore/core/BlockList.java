package fred.exploregalore.core;

import fred.exploregalore.ExploreGalore;
import fred.exploregalore.blocks.LivingLilyStemBlock;
import fred.exploregalore.blocks.MoonStoneBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockList {

    public static final Block MOON_STONE;
    public static final Block LIVING_LILY_STEM;

    static {
        MOON_STONE = registerBlock(new MoonStoneBlock(AbstractBlock.Settings.of(Material.STONE).strength(1.5f, 6.0f).luminance((state) -> MoonStoneBlock.isLit(state) ? 15 : 0)), "moon_stone");
        LIVING_LILY_STEM = registerBlock(new LivingLilyStemBlock(AbstractBlock.Settings.of(Material.PLANT).strength(0.1F).sounds(BlockSoundGroup.GRASS)), "living_lily_stem");
    }

    private static <T extends Block> T registerBlock(T block, String registryName) {
        return Registry.register(Registry.BLOCK, new Identifier(ExploreGalore.MOD_ID, registryName), block);
    }

    // Used to load the class - resulting in the execution of the static block.
    // The method itself does nothing - it just forces the jvm to initalize the class.
    public static void initalizeAndRegister() {}




}
