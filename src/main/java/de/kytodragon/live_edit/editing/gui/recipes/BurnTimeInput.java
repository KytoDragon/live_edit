package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.gui.components.Texture;
import de.kytodragon.live_edit.editing.gui.components.VanillaTextures;
import de.kytodragon.live_edit.editing.gui.components.Decal;
import de.kytodragon.live_edit.editing.gui.components.MyGuiComponent;
import de.kytodragon.live_edit.editing.gui.modules.ItemOrTagInput;
import de.kytodragon.live_edit.editing.gui.modules.TimeInput;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class BurnTimeInput extends MyGuiComponent implements IRecipeInput {

    private final ItemOrTagInput ingredient;
    private final TimeInput processing_time;
    private int progress;

    public BurnTimeInput(int x, int y) {
        super(x, y);

        ingredient = new ItemOrTagInput(20, 10, true, true, false);
        addChild(ingredient);
        processing_time = new TimeInput(10, 36, false);
        addChild(processing_time);
        addChild(new Decal(70, 13, VanillaTextures.BURN_EMPTY));
    }

    @Override
    public void setRecipe(MyRecipe recipe) {
        ingredient.setIngredient(recipe.ingredients.get(0));
        processing_time.setResult(recipe.results.get(0));
    }

    @Override
    public MyRecipe getRecipe() {
        MyRecipe recipe = new MyRecipe();
        recipe.ingredients = List.of(ingredient.getIngredient());
        recipe.results = List.of(processing_time.getResult());
        return recipe;
    }

    @Override
    public void tick() {
        super.tick();

        progress = (progress + 1) % (processing_time.getValue() + 1);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderForeground(graphics, partialTick, mouseX, mouseY);

        Texture texture = VanillaTextures.BURN_FILLED;
        int height = texture.height() * progress / processing_time.getValue();
        if (height > texture.height())
            height = texture.height();
        graphics.blit(texture.texture_id(), x+70, y+13 + height, texture.startX(), texture.startY() + height, texture.width(), texture.height() - height);
    }
}
