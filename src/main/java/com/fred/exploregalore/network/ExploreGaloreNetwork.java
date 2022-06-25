package com.fred.exploregalore.network;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.network.message.KeybindMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.net.http.HttpClient;

public final class ExploreGaloreNetwork {

    private ExploreGaloreNetwork(){}

    /**
     * Update this version when you add new content/break backwards compatibility to
     * notify servers when incoming information changes.
     */
    public static final String NETWORK_VERSION = "0.1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExploreGalore.MOD_ID, "main"), // "main" is the path of the ResourceLocation
            () -> NETWORK_VERSION, // Supplier of the network version
            NETWORK_VERSION::equals, // Client accepted versions (only exact version here)
            NETWORK_VERSION::equals // Server accepted versions (only exact version here)
    );

    public static void initialize() {
        // We need a unique index for each message
        CHANNEL.registerMessage(0, KeybindMessage.class, KeybindMessage::encode, KeybindMessage::decode, KeybindMessage::handleKeyAction);
    }

}
