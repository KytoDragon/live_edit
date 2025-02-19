package de.kytodragon.live_edit.editing.gui.recipes;

import de.kytodragon.live_edit.editing.EditCommandPacket;
import de.kytodragon.live_edit.editing.gui.GuiCommon;
import de.kytodragon.live_edit.integration.PacketRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.*;
import de.kytodragon.live_edit.editing.gui.modules.*;
import de.kytodragon.live_edit.recipe.RecipeType;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class RecipeEditingGui extends GuiCommon<RecipeEditingMenu> {

    public static final Map<Class<? extends MyIngredient>, IngredientInputFactory> ingredientMapper = new HashMap<>();
    public static final Map<Class<? extends MyResult>, ResultInputFactory> resultMapper = new HashMap<>();
    public static final Map<RecipeType, RecipeInputFactory> recipeMapper = new HashMap<>();

    private MyRecipe recipe;

    private IRecipeInput recipe_editor;
    private ScrolledListPanel ingredient_list;
    private ScrolledListPanel result_list;

    public RecipeEditingGui(RecipeEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        content.addChild(new Button(100, 80, 30, 12, "Save", this::sendRecipeToServer));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        /*if (menu.recipe_slot.id != null) {
            graphics.drawString(this.font, menu.recipe_slot.id.toString(), 10, 20, 0x404040);
        }*/
    }

    @Override
    protected void containerTick() {

        try {
            if (recipe == null) {
                recipe = (MyRecipe) menu.recipe_slot.getRecipe();

                if (recipe != null) {
                    RecipeInputFactory factory = recipeMapper.get(recipe.type);
                    if (factory != null) {
                        recipe_editor = factory.getGUIComponent(8, 20);
                        recipe_editor.setRecipe(recipe);
                        MyGuiComponent component = recipe_editor.getGUIComponent();
                        content.addChild(component);
                    } else {

                        ingredient_list = new ScrolledListPanel(8, 20, 80, 60);
                        int heigth = 0;
                        for (MyIngredient ingredient : recipe.ingredients) {
                            IngredientInputFactory inputFactory = ingredientMapper.get(ingredient.getClass());
                            if (inputFactory == null) {
                                LiveEditMod.LOGGER.error("Recipe editor: Could not find GUI-component for ingredient class {}", ingredient.getClass());
                                onClose();
                                return;
                            }
                            IIngredientInput input = inputFactory.getGUIComponent(0, heigth);
                            input.setIngredient(ingredient);
                            MyGuiComponent component = input.getGUIComponent();
                            heigth += component.height;
                            ingredient_list.addChild(component);
                        }
                        content.addChild(ingredient_list);

                        result_list = new ScrolledListPanel(88, 20, 80, 60);
                        heigth = 0;
                        for (MyResult result : recipe.results) {
                            ResultInputFactory inputFactory = resultMapper.get(result.getClass());
                            if (inputFactory == null) {
                                LiveEditMod.LOGGER.error("Recipe editor: Could not find GUI-component for result class {}", result.getClass());
                                onClose();
                                return;
                            }
                            IResultInput input = inputFactory.getGUIComponent(0, heigth);
                            input.setResult(result);
                            MyGuiComponent component = input.getGUIComponent();
                            heigth += component.height;
                            result_list.addChild(component);
                        }
                        content.addChild(result_list);
                    }
                    content.calculateBounds();
                }
            }
        } catch (Exception e) {
            LiveEditMod.LOGGER.error("Cought error in recipe editing GUI tick-Method", e);
            onClose();
        }

        super.containerTick();
    }

    private void sendRecipeToServer() {
        if (recipe == null)
            return;
        boolean was_new_recipe = recipe.results.isEmpty() && recipe.ingredients.isEmpty();

        MyRecipe new_recipe;
        if (recipe_editor != null) {
            new_recipe = recipe_editor.getRecipe();
            new_recipe.type = recipe.type;
            new_recipe.id = recipe.id;
            new_recipe.group = recipe.group;
        } else {
            new_recipe = new MyRecipe();
            new_recipe.type = recipe.type;
            new_recipe.id = recipe.id;
            new_recipe.group = recipe.group;
            new_recipe.shaped_width = recipe.shaped_width;
            new_recipe.ingredients = new ArrayList<>(ingredient_list.children.size());
            for (MyGuiComponent comp : ingredient_list.children) {
                IIngredientInput input = (IIngredientInput)comp;
                new_recipe.ingredients.add(input.getIngredient());
            }
            new_recipe.results = new ArrayList<>(result_list.children.size());
            for (MyGuiComponent comp : result_list.children) {
                IResultInput input = (IResultInput)comp;
                new_recipe.results.add(input.getResult());
            }
        }

        EditCommandPacket packet = new EditCommandPacket();
        if (was_new_recipe)
            packet.command = "add";
        else
            packet.command = "replace";
        packet.command += " recipe " + new_recipe.type.name() + " " + new_recipe.id.toString() + " " + new_recipe.toJsonString();
        PacketRegistry.INSTANCE.sendToServer(packet);

        onClose();
    }
}
