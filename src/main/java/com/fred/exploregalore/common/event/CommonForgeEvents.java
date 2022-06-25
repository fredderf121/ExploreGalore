package com.fred.exploregalore.common.event;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.commands.DrawBlockPathCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExploreGalore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEvents {

    private CommonForgeEvents() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent registerCommandEvent) {
        DrawBlockPathCommand.register(registerCommandEvent.getDispatcher());
        ExploreGalore.LOGGER.debug("Registering Explore Galore's Commands!");
    }
}
