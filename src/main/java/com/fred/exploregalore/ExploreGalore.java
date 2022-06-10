package com.fred.exploregalore;

import com.fred.exploregalore.commands.DrawBlockPathCommand;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("exploregalore")
public class ExploreGalore
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public ExploreGalore() {

        // Register ourselves for server and other game events we are interested in
        // This is REQUIRED for our @SubscribeEvent annotations to be found by Forge!
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LOGGER.info("Registering Explore Galore's Commands!");
        DrawBlockPathCommand.register(event.getDispatcher());
    }


}
