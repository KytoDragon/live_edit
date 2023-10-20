package de.kytodragon.live_edit.mixin_interfaces;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface IngredientTagValueInterface {
    TagKey<Item> live_edit_mixin_getTag();
}
