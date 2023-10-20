package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.CompoundIngredientInterface;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(IntersectionIngredient.class)
public class IntersectionIngredientMixin implements CompoundIngredientInterface {

    @Final
    @Shadow(remap = false)
    private List<Ingredient> children;

    public List<Ingredient> live_edit_mixin_getChildren() {
        return children;
    }
}
