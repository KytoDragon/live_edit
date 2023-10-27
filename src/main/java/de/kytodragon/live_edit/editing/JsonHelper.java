package de.kytodragon.live_edit.editing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class JsonHelper {

    public static ResourceLocation getResourceLocation(JsonObject json, String name) {
        return ResourceLocation.of(GsonHelper.getAsString(json, name), ':');
    }

    public static Item getItem(JsonObject json, String name) {
        Item item = ForgeRegistries.ITEMS.getValue(getResourceLocation(json, name));
        Objects.requireNonNull(item);
        return item;
    }

    public static Fluid getFluid(JsonObject json, String name) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(getResourceLocation(json, name));
        Objects.requireNonNull(fluid);
        return fluid;
    }

    public static TagKey<Item> getItemTag(JsonObject json, String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, getResourceLocation(json, name));
    }

    public static TagKey<Fluid> getFluidTag(JsonObject json, String name) {
        return TagKey.create(Registry.FLUID_REGISTRY, getResourceLocation(json, name));
    }

    @Nullable
    public static CompoundTag getNBTTag(JsonObject json, String name) {
        JsonObject tag = GsonHelper.getAsJsonObject(json, name, null);
        if (tag == null)
            return null;

        try {
            String tag_string = new Gson().toJson(tag);
            return TagParser.parseTag(tag_string);
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Failed to parse item nbt tag, reason: " + e.getMessage());
        }
    }
}
