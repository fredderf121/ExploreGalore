package com.fred.exploregalore.client.event;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.client.Keybinds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ExploreGalore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents(){}


    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        Keybinds.initializeKeyMappings();
    }
}
