package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.editing.MyResult;
import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.nio.file.Path;
import java.util.*;

public class VanillaIntegration implements Integration {

    public net.minecraft.world.item.crafting.RecipeManager vanilla_recipe_manager;
    public ITagManager<Item> forge_tag_manager;

    private MinecraftServer server;

    private Map<Item, Integer> current_burn_times;

    @Override
    public void registerManipulators(RecipeManager manager) {
        MyRecipe.ingredient_deserializers.put("item", MyIngredient.ItemIngredient::fromJson);
        MyRecipe.ingredient_deserializers.put("tag", MyIngredient.TagIngredient::fromJson);
        MyRecipe.ingredient_deserializers.put("fluid", MyIngredient.FluidIngredient::fromJson);
        MyRecipe.ingredient_deserializers.put("time", MyIngredient.TimeIngredient::fromJson);
        MyRecipe.ingredient_deserializers.put("item_list", MyIngredient.ItemListIngredient::fromJson);

        MyRecipe.result_deserializers.put("item", MyResult.ItemResult::fromJson);
        MyRecipe.result_deserializers.put("time", MyResult.TimeResult::fromJson);
        MyRecipe.result_deserializers.put("chance", MyResult.ChanceResult::fromJson);
        MyRecipe.result_deserializers.put("experience", MyResult.ExperienceResult::fromJson);
        MyRecipe.result_deserializers.put("tag", MyResult.TagResult::fromJson);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> VanillaUIIntegration::registerClientGui);

        manager.addRecipeManipulator(this, RecipeType.CRAFTING, new CraftingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMELTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMELTING));
        manager.addRecipeManipulator(this, RecipeType.BLASTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.BLASTING));
        manager.addRecipeManipulator(this, RecipeType.SMOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMOKING));
        manager.addRecipeManipulator(this, RecipeType.CAMPFIRE_COOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING));
        manager.addRecipeManipulator(this, RecipeType.STONECUTTING, new StoneCuttingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMITHING, new SmithingRecipeManipulator());

        manager.addRecipeManipulator(this, RecipeType.TAGS, new TagManipulator());
        manager.addRecipeManipulator(this, RecipeType.LOOT_TABLE, new LootTableManipulator());

        manager.addRecipeManipulator(this, RecipeType.BURN_TIME, new BurnTimeManipulator());
        manager.addRecipeManipulator(this, RecipeType.BREWING, new BrewingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.COMPOSTING, new CompostManipulator());

        MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTimeRequest);
    }

    @Override
    public void initServer(MinecraftServer server, Path data_path) {
        this.server = server;
        vanilla_recipe_manager = server.getRecipeManager();

        forge_tag_manager = ForgeRegistries.ITEMS.tags();
        Objects.requireNonNull(forge_tag_manager);
    }

    @Override
    public void shutdownServer(Path data_path) {

        current_burn_times = null;

        forge_tag_manager = null;
        vanilla_recipe_manager = null;
        server = null;
    }

    public void onFuelBurnTimeRequest(FurnaceFuelBurnTimeEvent event) {
        if (current_burn_times != null) {
            Integer burnTime = current_burn_times.get(event.getItemStack().getItem());
            if (burnTime != null) {
                event.setBurnTime(burnTime.intValue());
            } else {
                event.setBurnTime(0);
            }
        }
    }

    public Map<ResourceLocation, LootTable> getCurrentLootTables() {
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        server.getLootData().typeKeys.get(LootDataType.TABLE).forEach(id -> {
            tables.put(id, server.getLootData().getElement(new LootDataId<>(LootDataType.TABLE, id)));
        });
        return tables;
    }

}
