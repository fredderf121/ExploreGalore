package com.fred.exploregalore;

import com.fred.exploregalore.commands.DrawBlockPathCommand;
import com.fred.exploregalore.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExploreGalore.MOD_ID)
public class ExploreGalore {
    public static final String MOD_ID = "exploregalore";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public ExploreGalore() {

        // Register ourselves for server and other game events we are interested in
        // This is REQUIRED for our @SubscribeEvent annotations to be found by Forge!
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        // Something I don't quite understand yet, but this is a different event bus
        // required for registering items, blocks, etc.
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Items.ITEM_REGISTRY.register(eventBus);

    }

    public void onRegisterCommands(RegisterCommandsEvent registerCommandEvent) {
        LOGGER.info("Registering Explore Galore's Commands!");
        DrawBlockPathCommand.register(registerCommandEvent.getDispatcher());
    }






}
