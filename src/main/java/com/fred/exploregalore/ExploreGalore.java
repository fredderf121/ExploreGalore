package com.fred.exploregalore;

import com.fred.exploregalore.commands.DrawBlockPathCommand;
import com.fred.exploregalore.item.ExploreGaloreItems;
import com.fred.exploregalore.network.ExploreGaloreNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ExploreGaloreItems.ITEM_REGISTRY.register(modEventBus);

    }









}
