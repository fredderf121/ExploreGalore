package com.fred.exploregalore.client.event;

import com.fred.exploregalore.ExploreGalore;
import com.fred.exploregalore.client.Keybinds;
import com.fred.exploregalore.item.ExploreGaloreItems;
import com.fred.exploregalore.network.ExploreGaloreNetwork;
import com.fred.exploregalore.network.message.KeybindMessage;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Followed this <a href="https://youtu.be/RFeGW8xKPDY">Keybind tutorial for 1.16</a>
 */
@Mod.EventBusSubscriber(modid = ExploreGalore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientForgeEvents {

    private ClientForgeEvents(){}

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent keyInputEvent) {
        Minecraft minecraft = Minecraft.getInstance();

        // We only handle events when a world is open, and guis like the inventory aren't open.
        if (minecraft.level == null) {
            return;
        }

        handleInGameKeyPress(minecraft, keyInputEvent.getKey(), keyInputEvent.getAction(), keyInputEvent.getModifiers());
    }

    /**
     *
     * @param key The keyboard key code that was pressed
     * @param action Press, release, or repeated
     * @param modifiers Ctrl, alt, shift, represented as a bit-field
     */
    private static void handleInGameKeyPress(Minecraft minecraft, int key, int action, int modifiers) {
        // We only handle events that occur while the user's game is open, and there is NOT
        // any guis/screens open.
        if (minecraft.screen != null) {
            return;
        }
        val localPlayer = minecraft.player;
        // I can't see when the player can ever be null in this scenario, but the IDE complains.
        if (localPlayer == null) {
            return;
        }
        if (action == GLFW.GLFW_PRESS) {
            if (key == Keybinds.BUILDERS_WAND_SWITCH_MODE.getKey().getValue()) {
                // Sending info to server to trigger a building wand switch mode.
                if (localPlayer.getMainHandItem().is(ExploreGaloreItems.BUILDERS_WAND.get())) {
                    ExploreGaloreNetwork.CHANNEL.sendToServer(new KeybindMessage(key));
                }
            }
        }

    }

}
