package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.components.ItemComponent;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ChanceInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ComposterInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput ingredient;
    private final ChanceInput compost_chance;

    public ComposterInput(int x, int y) {
        super(x, y);

        ingredient = new ItemOrTagInput(20, 10, true, true, false);
        addChild(ingredient);
        compost_chance = new ChanceInput(90, 10);
        addChild(compost_chance);
        ItemComponent composter = new ItemComponent(60, 10);
        composter.itemstack = new ItemStack(Items.COMPOSTER);
        composter.can_change = false;
        composter.no_background = true;
        addChild(composter);
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        ingredient.setIngredient(recipe.ingredients.get(0));
        compost_chance.setResult(recipe.results.get(0));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(ingredient.getIngredient());
        recipe.results = List.of(compost_chance.getResult());
        return recipe;
    }
}
