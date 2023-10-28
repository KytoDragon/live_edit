package de.kytodragon.live_edit.editing.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.kytodragon.live_edit.LiveEditMod;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class RecipeEditingGui extends AbstractContainerScreen<RecipeEditingMenu> {

    private static final ResourceLocation MENU_TYPE_ID = new ResourceLocation(LiveEditMod.MODID, "recipe_editing_menu");

    public RecipeEditingGui(RecipeEditingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 176;

        this.inventoryLabelY = this.imageHeight - 94;
    }

    public static void clientSetup(RegisterEvent event) {
        if (event.getRegistryKey() == ForgeRegistries.MENU_TYPES.getRegistryKey()) {
            ForgeRegistries.MENU_TYPES.register(MENU_TYPE_ID, RecipeEditingMenu.MENU_TYPE);
            MenuScreens.register(RecipeEditingMenu.MENU_TYPE, RecipeEditingGui::new);
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);

        super.renderTooltip(pose, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
        super.renderLabels(pose, mouseX, mouseY);
        drawString(pose, font, "Test", 10, 20, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        pose.pushPose();
        pose.translate(this.leftPos, this.topPos, 0);

        for (int y = 0; y < this.imageHeight; y += 16) {
            for (int x = 0; x < this.imageWidth; x += 16) {
                Texture texture;
                if (y == 0) {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_UPPER_LEFT;
                    } else if (x + 16 >= this.imageWidth) {
                        texture = VanillaTextures.BACKGROUND_UPPER_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_UPPER;
                    }
                } else if (y + 16 >= this.imageHeight) {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_LOWER_LEFT;
                    } else if (x + 16 >= this.imageWidth) {
                        texture = VanillaTextures.BACKGROUND_LOWER_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_LOWER;
                    }
                } else {
                    if (x == 0) {
                        texture = VanillaTextures.BACKGROUND_LEFT;
                    } else if (x + 16 >= this.imageWidth) {
                        texture = VanillaTextures.BACKGROUND_RIGHT;
                    } else {
                        texture = VanillaTextures.BACKGROUND_MIDDLE;
                    }
                }
                texture.draw(this, pose, x, y);
            }
        }

        menu.inventoryGui.renderBackground(pose);
        pose.popPose();
    }
}
