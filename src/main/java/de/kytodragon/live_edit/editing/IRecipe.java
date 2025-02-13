package de.kytodragon.live_edit.editing;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface IRecipe {
    JsonObject toJson();
    ResourceLocation getId();
    boolean containsItem(Item item);
}
