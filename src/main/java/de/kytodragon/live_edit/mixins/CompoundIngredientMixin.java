package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.CompoundIngredientInterface;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(CompoundIngredient.class)
public class CompoundIngredientMixin implements CompoundIngredientInterface {

    @Shadow(remap = false)
    private List<Ingredient> children;

    @Unique
    public List<Ingredient> live_edit_mixin_getChildren() {
        return children;
    }
}
