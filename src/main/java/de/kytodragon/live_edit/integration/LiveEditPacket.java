package de.kytodragon.live_edit.integration;

import net.minecraft.network.FriendlyByteBuf;

public interface LiveEditPacket {

    void encode(FriendlyByteBuf buf);
    void decode(FriendlyByteBuf buf);
}
