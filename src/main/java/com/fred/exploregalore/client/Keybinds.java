package com.fred.exploregalore.client;

import com.fred.exploregalore.ExploreGalore;
import com.mojang.blaze3d.platform.InputConstants;
import lombok.val;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public final class Keybinds {

    private Keybinds(){}

    private static final String EXPLORE_GALORE_KEY_CATEGORY = "key.category." + ExploreGalore.MOD_ID;

    public static final KeyMapping BUILDERS_WAND_SWITCH_MODE = registerKey("builders_wand_switch_mode", InputConstants.KEY_X, EXPLORE_GALORE_KEY_CATEGORY);



    public static void initializeKeyMappings() {
        // Lazily initializes the static values of the class; allows for final values
    }

    private static KeyMapping registerKey(String name, int keycode, String category) {
        // I believe a new category is automatically added if it does not already exist.
        val key = new KeyMapping("key." + ExploreGalore.MOD_ID + "." + name, keycode, category);
        ClientRegistry.registerKeyBinding(key);
        return key;
    }

}
