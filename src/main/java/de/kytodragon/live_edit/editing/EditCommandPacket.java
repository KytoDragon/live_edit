package de.kytodragon.live_edit.editing;

import de.kytodragon.live_edit.integration.LiveEditPacket;
import net.minecraft.network.FriendlyByteBuf;

public class EditCommandPacket implements LiveEditPacket {

    public String command;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(command);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        command = buf.readUtf();
    }
}
