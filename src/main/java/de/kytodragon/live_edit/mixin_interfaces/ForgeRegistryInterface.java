package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

public interface ForgeRegistryInterface<T> extends IForgeRegistry<Item> {

    void live_edit_mixin_replaceTags(Map<TagKey<T>, HolderSet.Named<T>> tags);
}
