package de.kytodragon.live_edit.editing.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.Texture;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ExperienceInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;
import de.kytodragon.live_edit.editing.gui.modules.TimeInput;

import java.util.List;

public class SmeltingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput ingredient;
    private final ItemInput result;
    private final TimeInput processing_time;
    private final ExperienceInput experience;
    private int progress;

    public SmeltingRecipeInput(int x, int y) {
        super(x, y);

        ingredient = new ItemOrTagInput(20, 10, true, true, false);
        children.add(ingredient);
        result = new ItemInput(103, 10, true, false, true, true);
        children.add(result);
        processing_time = new TimeInput(10, 36, false);
        children.add(processing_time);
        experience = new ExperienceInput(90, 36);
        children.add(experience);
        children.add(new Decal(65, 11, VanillaTextures.ARROW_RIGHT));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        ingredient.setIngredient(recipe.ingredients.get(0));
        processing_time.setIngredient(recipe.ingredients.get(1));
        result.setResult(recipe.results.get(0));
        experience.setResult(recipe.results.get(1));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(ingredient.getIngredient(), processing_time.getIngredient());
        recipe.results = List.of(result.getResult(), experience.getResult());
        return recipe;
    }

    @Override
    public void tick() {
        progress = (progress + 1) % (processing_time.getValue() + 1);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        super.renderForeground(pose, partialTick, mouseX, mouseY);

        Texture texture = VanillaTextures.ARROW_RIGHT_FILLED;
        int width = texture.width() * progress / processing_time.getValue();
        if (width > texture.width())
            width = texture.width();
        RenderSystem.setShaderTexture(0, texture.texture_id());
        this.blit(pose, x+65, y+11, texture.startX(), texture.startY(), width, texture.height());
    }
}
