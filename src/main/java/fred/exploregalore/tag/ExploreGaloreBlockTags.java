package fred.exploregalore.tag;

import fred.exploregalore.ExploreGalore;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ExploreGaloreBlockTags {
    public static final Tag<Block> LIVING_LILY_STEM_PLACEABLE;

    /**
     * Objects are not to be created from this class - this class serves as a collection of tags and has no individual
     * properties.
     */
    private ExploreGaloreBlockTags(){

    }

   /* private static Tag.Identified<Block> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<Block> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }*/

    static {
        LIVING_LILY_STEM_PLACEABLE = TagRegistry.block(new Identifier(ExploreGalore.MOD_ID,"living_lily_stem_placeable"));
    }
}
