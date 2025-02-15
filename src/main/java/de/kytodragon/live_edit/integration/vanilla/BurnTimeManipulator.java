package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BurnTimeManipulator extends IRecipeManipulator<BurnTime, MyRecipe, VanillaIntegration> {

    protected BurnTimeManipulator() {
        super(MyRecipe::fromJson);
    }

    @Override
    public ResourceLocation getKey(BurnTime burnTime) {
        return ForgeRegistries.ITEMS.getKey(burnTime.item());
    }

    @Override
    public Collection<BurnTime> getCurrentRecipes() {
        List<BurnTime> result = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {

            int burnTime = ForgeHooks.getBurnTime(new ItemStack(item), null);
            if (burnTime > 0)
                result.add(new BurnTime(item, burnTime));
        }
        return result;
    }

    @Override
    public Optional<BurnTime> getRecipe(ResourceLocation key) {
        Item item = ForgeRegistries.ITEMS.getValue(key);
        if (item == null)
            return Optional.empty();
        return Optional.of(new BurnTime(item, ForgeHooks.getBurnTime(new ItemStack(item), null)));
    }

    @Override
    public MyRecipe encodeRecipe(BurnTime recipe) {
        MyRecipe result = new MyRecipe();
        result.id = getKey(recipe);
        result.ingredients = List.of(new MyIngredient.ItemIngredient(recipe.item()));
        result.results = List.of(new MyResult.TimeResult(recipe.burn_time()));
        result.type = RecipeType.BURN_TIME;
        return result;
    }

    @Override
    protected void exportDeleted(StringBuilder sb, ResourceLocation id) {
        sb.append("<item:");
        sb.append(id);
        sb.append(">.burnTime = 0;");
    }

    @Override
    protected void exportAdded(StringBuilder sb, MyRecipe recipe) {
        recipe.ingredients.get(0).export(sb);
        sb.append(".burnTime = ");
        recipe.results.get(0).export(sb);
        sb.append(";");
    }
}
