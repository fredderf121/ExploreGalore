package com.fred.exploregalore.network.message;

import com.fred.exploregalore.client.Keybinds;
import com.fred.exploregalore.item.builderswand.BuildersWand;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.val;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record KeybindMessage(@Getter int keyCode) {


    /**
     * Of type {@code BiConsumer<MSG, FriendlyByteBuf>}.
     *
     * @see net.minecraftforge.network.simple.SimpleChannel#registerMessage
     */
    public static void encode(KeybindMessage message, ByteBuf buffer) {
        buffer.writeInt(message.keyCode());
    }

    /**
     * Of type {@code Function<FriendlyByteBuf, MSG>}.
     *
     * @see net.minecraftforge.network.simple.SimpleChannel#registerMessage
     */
    public static KeybindMessage decode(ByteBuf buffer) {
        return new KeybindMessage(buffer.readInt());
    }

    /**
     * Of type {@code BiConsumer<MSG, Supplier<NetworkEvent.Context>>}.
     */
    public static void handleKeyAction(KeybindMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        val context = contextSupplier.get();

        context.enqueueWork(() -> {
            // Not sure if the sender can ever be null?
            val serverPlayer = Optional.ofNullable(context.getSender())
                    .orElseThrow(() -> new RuntimeException("The ServerPlayer is null when trying to handle a key press. " +
                            "Possibly the packet was sent in the wrong direction?"));

            if (message.keyCode() == Keybinds.BUILDERS_WAND_SWITCH_MODE.getKey().getValue()) {
                BuildersWand.toggleBuildingMode(serverPlayer);
            }
        });


    }
}
