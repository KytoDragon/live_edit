package de.kytodragon.live_edit.integration;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public class PacketRegistry {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(LiveEditMod.MODID, "update_vanilla"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    public static int PACKET_ID = 1;

    public static <T extends LiveEditPacket> void registerServerPacket(Class<T> clazz, Supplier<T> constuctor) {
        PacketRegistry.INSTANCE.registerMessage(PacketRegistry.PACKET_ID++, clazz,
            PacketRegistry::encodeMessage, (buf) -> {
                T packet = constuctor.get();
                packet.decode(buf);
                return packet;
            },
            PacketRegistry::handleServerMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void encodeMessage(LiveEditPacket packet, FriendlyByteBuf buf) {
        packet.encode(buf);
    }

    public static void handleServerMessage(LiveEditPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            RecipeManager.instance.handleServerPacket(packet, context.get().getSender());
        });
        context.get().setPacketHandled(true);
    }
}
