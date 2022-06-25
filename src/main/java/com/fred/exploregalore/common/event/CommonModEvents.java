package com.fred.exploregalore.common.event;


import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.network.ExploreGaloreNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ExploreGalore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonModEvents {

    private CommonModEvents() {
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent commonSetupEvent) {
        ExploreGaloreNetwork.initialize();

        ExploreGalore.LOGGER.debug("Completed commonSetupEvent for Explore Galore!");

    }

}
