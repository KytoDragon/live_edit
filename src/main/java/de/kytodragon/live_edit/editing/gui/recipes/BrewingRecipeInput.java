package de.kytodragon.live_edit.editing.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.Texture;
import de.kytodragon.live_edit.editing.gui.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ItemInput;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;

import java.util.List;

public class BrewingRecipeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput base;
    private final ItemOrTagInput addition;
    private final ItemInput result;
    private int progress;
    private static final int BREWING_TIME = 400;

    public BrewingRecipeInput(int x, int y) {
        super(x, y);

        base = new ItemOrTagInput(45, 34, true, true, false);
        children.add(base);
        addition = new ItemOrTagInput(70, 0, true, true, false);
        children.add(addition);
        result = new ItemInput(93, 34, true, false, true, false);
        children.add(result);
        children.add(new Decal(60, 5, VanillaTextures.ARROW_DOWN));
        children.add(new Decal(45, 34, VanillaTextures.POTION_SLOT));
        children.add(new Decal(93, 34, VanillaTextures.POTION_SLOT));
        children.add(new Decal(64, 17, VanillaTextures.BREWING_PIPES));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        base.setIngredient(recipe.ingredients.get(0));
        addition.setIngredient(recipe.ingredients.get(1));
        result.setResult(recipe.results.get(0));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(base.getIngredient(), addition.getIngredient());
        recipe.results = List.of(result.getResult());
        return recipe;
    }

    @Override
    public void tick() {
        super.tick();

        progress = (progress + 1) % (BREWING_TIME + 1);
    }

    @Override
    public void renderForeground(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        super.renderForeground(pose, partialTick, mouseX, mouseY);

        Texture texture = VanillaTextures.ARROW_DOWN_FILLED;
        int height = texture.height() * progress / BREWING_TIME;
        if (height > texture.height())
            height = texture.height();
        RenderSystem.setShaderTexture(0, texture.texture_id());
        this.blit(pose, x+60, y+5, texture.startX(), texture.startY(), texture.width(), height);
    }
}
