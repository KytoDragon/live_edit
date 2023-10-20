package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.IngredientTagValueInterface;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.TagValue.class)
public class IngredientTagValueMixin implements IngredientTagValueInterface {

    @Final
    @Shadow
    private TagKey<Item> tag;

    public TagKey<Item> live_edit_mixin_getTag() {
        return tag;
    }
}
