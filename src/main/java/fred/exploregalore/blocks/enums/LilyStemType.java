package fred.exploregalore.blocks.enums;

import net.minecraft.util.StringIdentifiable;

public enum LilyStemType implements StringIdentifiable {

    BASE("base"),
    DEFAULT("default"),
    ATTACHED_MIDDLE("attached_middle"),
    ATTACHED_TOP("attached_top");


    private final String name;

    LilyStemType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
