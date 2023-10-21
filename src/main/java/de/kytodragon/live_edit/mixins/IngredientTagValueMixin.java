package de.kytodragon.live_edit.mixins;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.TagValue.class)
public interface IngredientTagValueMixin {

    @Accessor("tag")
    TagKey<Item> live_edit_mixin_getTag();
}
