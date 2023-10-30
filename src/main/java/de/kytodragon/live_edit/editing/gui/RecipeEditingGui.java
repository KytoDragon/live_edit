package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.LiveEditMod;
import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.editing.gui.components.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

public class RecipeEditingGui extends AbstractContainerScreen<RecipeEditingMenu> {

    private static final ResourceLocation MENU_TYPE_ID = new ResourceLocation(LiveEditMod.MODID, "recipe_editing_menu");

    private final List<MyGuiComponent> components = new ArrayList<>();
    private MyRecipe recipe;

    public RecipeEditingGui(RecipeEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 176;

        this.inventoryLabelY = this.imageHeight - 94;

        components.add(new Background(0, 0, 176, 176));
        components.add(menu.inventoryGui);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseClicked(mouseX, mouseY, button, menu.getCarried()))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        for (MyGuiComponent component : components) {
            if (component.mouseReleased(mouseX, mouseY, button))
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
                ScrolledListPanel ingredientList = new ScrolledListPanel(8, 20, 80, 60);
                int heigth = 0;
                for (MyIngredient ingredient : recipe.ingredients) {
                    if (ingredient instanceof MyIngredient.ItemIngredient item) {
                        ingredientList.children.add(new ItemComponent(0, heigth+1, item, false));
                    } else if (ingredient instanceof MyIngredient.TimeIngredient timeIngredient) {
                        ingredientList.children.add(new IntegerInput(1, heigth+1, 40, 12, timeIngredient.processing_time));
                    } else {
                        ingredientList.children.add(new Decal(0, heigth + 1, VanillaTextures.EMPTY_SLOT));
                        ingredientList.children.add(new TextComponent(20, heigth + 1, ingredient.toString()));
                    }
                    heigth += 20;
                }
                components.add(ingredientList);

                ScrolledListPanel resultList = new ScrolledListPanel(88, 20, 80, 60);
                heigth = 0;
                for (MyResult result : recipe.results) {
                    if (result instanceof MyResult.ItemResult item) {
                        resultList.children.add(new ItemComponent(0, heigth+1, item, false));
                    } else if (result instanceof MyResult.ExperienceResult experienceResult) {
                        resultList.children.add(new IntegerInput(1, heigth+1, 40, 12, (int)experienceResult.experience));
                    } else {
                        resultList.children.add(new Decal(0, heigth + 1, VanillaTextures.EMPTY_SLOT));
                        resultList.children.add(new TextComponent(20, heigth + 1, result.toString()));
                    }
                    heigth += 20;
                }
                components.add(resultList);
            }
        }
    }
}
