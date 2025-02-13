package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.modules.*;
import de.kytodragon.live_edit.editing.gui.recipes.*;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VanillaUIIntegration {

    public static void registerClientGui() {

        RecipeEditingGui.ingredientMapper.put(MyIngredient.ItemIngredient.class, ItemOrTagInput::new);
        RecipeEditingGui.ingredientMapper.put(MyIngredient.TagIngredient.class, ItemOrTagInput::new);
        RecipeEditingGui.ingredientMapper.put(MyIngredient.TimeIngredient.class, TimeInput::new);

        RecipeEditingGui.resultMapper.put(MyResult.ItemResult.class, ItemInput::new);
        RecipeEditingGui.resultMapper.put(MyResult.ExperienceResult.class, ExperienceInput::new);
        RecipeEditingGui.resultMapper.put(MyResult.TimeResult.class, TimeInput::new);
        RecipeEditingGui.resultMapper.put(MyResult.ChanceResult.class, ChanceInput::new);

        RecipeEditingGui.recipeMapper.put(RecipeType.CRAFTING, CraftingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.SMELTING, SmeltingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.CAMPFIRE_COOKING, SmeltingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.SMOKING, SmeltingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.BLASTING, SmeltingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.STONECUTTING, StoneCuttingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.SMITHING, SmithingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.BURN_TIME, BurnTimeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.COMPOSTING, ComposterInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.BREWING, BrewingRecipeInput::new);
        RecipeEditingGui.recipeMapper.put(RecipeType.TAGS, TagAssignmentInput::new);
    }
}
