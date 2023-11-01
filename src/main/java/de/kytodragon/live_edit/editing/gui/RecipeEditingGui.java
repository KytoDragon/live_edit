package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.*;
import de.kytodragon.live_edit.editing.gui.modules.*;
import de.kytodragon.live_edit.recipe.RecipeType;

import java.util.*;

public class RecipeEditingGui extends AbstractContainerScreen<RecipeEditingMenu> {

    private static final ResourceLocation MENU_TYPE_ID = new ResourceLocation(LiveEditMod.MODID, "recipe_editing_menu");
    public static final Map<Class<? extends MyIngredient>, IngredientInputFactory> ingredientMapper = new HashMap<>();
    public static final Map<Class<? extends MyResult>, ResultInputFactory> resultMapper = new HashMap<>();
    public static final Map<RecipeType, RecipeInputFactory> recipeMapper = new HashMap<>();

    private final List<MyGuiComponent> components = new ArrayList<>();
    private MyRecipe recipe;

    private IRecipeInput recipe_editor;
    private ScrolledListPanel ingredient_list;
    private ScrolledListPanel result_list;

    public RecipeEditingGui(RecipeEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 176;

        this.inventoryLabelY = this.imageHeight - 94;

        components.add(new Background(0, 0, 176, 176));
        components.add(menu.inventoryGui);
        components.add(new Button(100, 80, 30, 12, "Save", this::sendRecipeToServer));
    }

    public static void clientSetup(RegisterEvent event) {
        if (event.getRegistryKey() == ForgeRegistries.MENU_TYPES.getRegistryKey()) {
            ForgeRegistries.MENU_TYPES.register(MENU_TYPE_ID, RecipeEditingMenu.MENU_TYPE);
            MenuScreens.register(RecipeEditingMenu.MENU_TYPE, RecipeEditingGui::new);
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);
        for (MyGuiComponent component : components) {
            component.renderForeground(pose, partialTick, mouseX - this.leftPos, mouseY - this.topPos);
        }
        pose.popPose();

        super.renderTooltip(pose, mouseX, mouseY);

        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);
        for (MyGuiComponent component : components) {
            component.renderOverlay(pose, partialTick, mouseX - this.leftPos, mouseY - this.topPos);
        }
        pose.popPose();
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
        super.renderLabels(pose, mouseX, mouseY);
        if (menu.recipe_slot.id != null) {
            //this.font.draw(pose, menu.recipe_slot.id.toString(), 10, 20, 0x404040);
        }
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            component.renderBackground(pose, partialTick, mouseX, mouseY);
        }

        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse_button) {
        super.mouseClicked(mouseX, mouseY, mouse_button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseClicked(mouseX, mouseY, mouse_button, menu.getCarried()))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouse_button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseDragged(mouseX, mouseY, mouse_button, deltaX, deltaY))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse_button) {
        super.mouseReleased(mouseX, mouseY, mouse_button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseReleased(mouseX, mouseY, mouse_button))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (super.mouseScrolled(mouseX, mouseY, scroll))
            return true;

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int unknown) {
        if (super.keyPressed(key, scancode, unknown))
            return true;

        for (MyGuiComponent component : components) {
            if (component.keyPressed(key, scancode, unknown))
                return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char character, int scancode) {
        if (super.charTyped(character, scancode))
            return true;

        for (MyGuiComponent component : components) {
            if (component.charTyped(character, scancode))
                return true;
        }
        return false;
    }

    @Override
    protected void containerTick() {

        if (recipe == null) {
            recipe = menu.recipe_slot.getRecipe();

            if (recipe != null) {
                RecipeInputFactory factory = recipeMapper.get(recipe.type);
                if (factory != null) {
                    recipe_editor = factory.getGUIComponent(8, 20);
                    recipe_editor.setRecipe(recipe);
                    components.add(recipe_editor.getGUIComponent());
                } else {

                    ingredient_list = new ScrolledListPanel(8, 20, 80, 60);
                    int heigth = 0;
                    for (MyIngredient ingredient : recipe.ingredients) {
                        IngredientInputFactory inputFactory = ingredientMapper.get(ingredient.getClass());
                        if (inputFactory == null) {
                            LiveEditMod.LOGGER.error("Recipe editor: Could not find GUI-component for ingredient class " + ingredient.getClass());
                            onClose();
                            return;
                        }
                        IIngredientInput input = inputFactory.getGUIComponent(0, heigth);
                        input.setIngredient(ingredient);
                        ingredient_list.children.add(input.getGUIComponent());
                        heigth += input.getGUIComponent().height;
                    }
                    components.add(ingredient_list);

                    result_list = new ScrolledListPanel(88, 20, 80, 60);
                    heigth = 0;
                    for (MyResult result : recipe.results) {
                        ResultInputFactory inputFactory = resultMapper.get(result.getClass());
                        if (inputFactory == null) {
                            LiveEditMod.LOGGER.error("Recipe editor: Could not find GUI-component for result class " + result.getClass());
                            onClose();
                            return;
                        }
                        IResultInput input = inputFactory.getGUIComponent(0, heigth);
                        input.setResult(result);
                        result_list.children.add(input.getGUIComponent());
                        heigth += input.getGUIComponent().height;
                    }
                    components.add(result_list);
                }
            }
        }

        for (MyGuiComponent component : components) {
            component.tick();
        }
    }

    private void sendRecipeToServer() {
        if (recipe == null)
            return;

        MyRecipe new_recipe;
        if (recipe_editor != null) {
            new_recipe = recipe_editor.getRecipe();
        } else {
            new_recipe = new MyRecipe();
            new_recipe.type = recipe.type;
            new_recipe.id = recipe.id;
            new_recipe.group = recipe.group;
            new_recipe.is_shaped = recipe.is_shaped;
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
        String command = "live-edit replace recipe " + new_recipe.type.name() + " " + new_recipe.id.toString() + " " + new_recipe.toJsonString();
        Objects.requireNonNull(minecraft);
        Objects.requireNonNull(minecraft.player);
        minecraft.player.commandUnsigned(command);

        onClose();
    }

    @Override
    public void onClose() {
        super.onClose();
        MyGuiComponent.setFocusOn(null);
    }
}
