package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.integration.PacketRegistry;
import de.kytodragon.live_edit.recipe.RecipeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public record VanillaUpdatePacket(List<BurnTime> new_burn_times,
                                  List<CompostChance> new_compostables,
                                  List<IBrewingRecipe> new_potions) {

    public static void registerPacketHandler() {
        PacketRegistry.INSTANCE.registerMessage(PacketRegistry.PACKET_ID++, VanillaUpdatePacket.class,
            VanillaUpdatePacket::encodeMessage, VanillaUpdatePacket::decodeMessage,
            VanillaUpdatePacket::handleMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static void encodeMessage(VanillaUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.new_burn_times, (buf2, burn_time) -> {
            buf2.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(burn_time.item())));
            buf2.writeInt(burn_time.burn_time());
        });
        buf.writeCollection(packet.new_compostables, (buf2, compostable) -> {
            buf2.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(compostable.item())));
            buf2.writeFloat(compostable.compastChance());
        });
        buf.writeCollection(packet.new_potions, (buf2, _recipe) -> {
            BrewingRecipe recipe = (BrewingRecipe)_recipe;
            recipe.getInput().toNetwork(buf2);
            recipe.getIngredient().toNetwork(buf2);
            buf2.writeItem(recipe.getOutput());
        });
    }

    private static VanillaUpdatePacket decodeMessage(FriendlyByteBuf buf) {
        List<BurnTime> new_burn_time = buf.readList(buf2 -> {
            return new BurnTime(ForgeRegistries.ITEMS.getValue(buf2.readResourceLocation()), buf2.readInt());
        });
        List<CompostChance> new_compostables = buf.readList(buf2 -> {
            return new CompostChance(ForgeRegistries.ITEMS.getValue(buf2.readResourceLocation()), buf2.readFloat());
        });
        List<IBrewingRecipe> new_potions = buf.readList(buf2 -> {
            Ingredient input = Ingredient.fromNetwork(buf2);
            Ingredient ingredient = Ingredient.fromNetwork(buf2);
            ItemStack output = buf2.readItem();
            return new BrewingRecipe(input, ingredient, output);
        });
        return new VanillaUpdatePacket(new_burn_time, new_compostables, new_potions);
    }

    private static void handleMessage(VanillaUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            RecipeManager.instance.handleClientPacket(packet);
        });
        context.get().setPacketHandled(true);
    }
}
