package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ForgeRegistryInterface<T> {

    void live_edit_mixin_replaceTags(Map<TagKey<T>, HolderSet.Named<T>> tags);
}
