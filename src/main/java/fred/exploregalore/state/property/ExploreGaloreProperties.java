package fred.exploregalore.state.property;

import fred.exploregalore.blocks.enums.LilyStemType;
import net.minecraft.state.property.EnumProperty;

public class ExploreGaloreProperties {

    public static final EnumProperty<LilyStemType> LILY_STEM_TYPE;

    static {
        LILY_STEM_TYPE = EnumProperty.of("lily_stem_type", LilyStemType.class);
    }
}
