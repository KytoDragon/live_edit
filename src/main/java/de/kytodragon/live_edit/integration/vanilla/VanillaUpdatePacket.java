package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.integration.LiveEditPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public class VanillaUpdatePacket implements LiveEditPacket {

    public List<BurnTime> new_burn_times = List.of();
    public List<CompostChance> new_compostables = List.of();
    public List<IBrewingRecipe> new_potions = List.of();

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(new_burn_times, (buf2, burn_time) -> {
            buf2.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(burn_time.item())));
            buf2.writeInt(burn_time.burn_time());
        });
        buf.writeCollection(new_compostables, (buf2, compostable) -> {
            buf2.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(compostable.item())));
            buf2.writeFloat(compostable.compastChance());
        });
        buf.writeCollection(new_potions, (buf2, _recipe) -> {
            BrewingRecipe recipe = (BrewingRecipe)_recipe;
            recipe.getInput().toNetwork(buf2);
            recipe.getIngredient().toNetwork(buf2);
            buf2.writeItem(recipe.getOutput());
        });
    }

    public void decode(FriendlyByteBuf buf) {
        new_burn_times = buf.readList(buf2 -> {
            return new BurnTime(ForgeRegistries.ITEMS.getValue(buf2.readResourceLocation()), buf2.readInt());
        });
        new_compostables = buf.readList(buf2 -> {
            return new CompostChance(ForgeRegistries.ITEMS.getValue(buf2.readResourceLocation()), buf2.readFloat());
        });
        new_potions = buf.readList(buf2 -> {
            Ingredient input = Ingredient.fromNetwork(buf2);
            Ingredient ingredient = Ingredient.fromNetwork(buf2);
            ItemStack output = buf2.readItem();
            return new BrewingRecipe(input, ingredient, output);
        });
    }
}
