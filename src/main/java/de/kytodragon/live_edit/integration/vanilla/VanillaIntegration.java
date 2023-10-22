package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.integration.Integration;
import de.kytodragon.live_edit.mixin_interfaces.BrewingRecipeRegistryInterface;
import de.kytodragon.live_edit.mixin_interfaces.ForgeRegistryInterface;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.*;

public class VanillaIntegration implements Integration {

    public net.minecraft.world.item.crafting.RecipeManager vanilla_recipe_manager;
    public ITagManager<Item> forge_tag_manager;

    private Registry<Item> vanilla_item_registry;
    private ForgeRegistryInterface<Item> forge_item_registry;
    private MinecraftServer server;

    private NewServerData server_data;

    private Map<Item, Integer> current_burn_times;

    @Override
    public void registerManipulators(RecipeManager manager) {
        // Deal with recipe types in the standard recipe manager that are not beeing handled by a manipulator.
        // This makes shure we do not delete recipes we do not know about.
        for (net.minecraft.world.item.crafting.RecipeType<?> recipeType : ForgeRegistries.RECIPE_TYPES.getValues()) {
            manager.addRecipeManipulator(this, new RecipeType("Dummy", recipeType), new DummyRecipeManipulator<>(recipeType));
        }

        manager.addRecipeManipulator(this, RecipeType.CRAFTING, new CraftingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMELTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMELTING, SmeltingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.BLASTING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.BLASTING, BlastingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.SMOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.SMOKING, SmokingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.CAMPFIRE_COOKING, new CoockingRecipeManipulator<>(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING, CampfireCookingRecipe::new));
        manager.addRecipeManipulator(this, RecipeType.STONECUTTING, new StoneCuttingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.SMITHING, new SmithingRecipeManipulator());

        manager.addRecipeManipulator(this, RecipeType.TAGS, new TagManipulator());

        manager.addRecipeManipulator(this, RecipeType.BURN_TIME, new BurnTimeManipulator());
        manager.addRecipeManipulator(this, RecipeType.BREWING, new BrewingRecipeManipulator());
        manager.addRecipeManipulator(this, RecipeType.COMPOSTING, new CompostManipulator());

        MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTimeRequest);
    }

    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    public void initServer(MinecraftServer server) {
        this.server = server;
        vanilla_recipe_manager = server.getRecipeManager();
        vanilla_item_registry = Registry.ITEM;
        forge_item_registry = ((ForgeRegistryInterface<Item>) ForgeRegistries.ITEMS);

        forge_tag_manager = ForgeRegistries.ITEMS.tags();
        Objects.requireNonNull(forge_tag_manager);
    }

    @Override
    public void shutdownServer() {

        current_burn_times = null;
        server_data = null;

        forge_tag_manager = null;
        forge_item_registry = null;
        vanilla_item_registry = null;
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

    @Override
    public void prepareReload() {
        server_data = new NewServerData();
        Ingredient.invalidateAll();
    }

    @Override
    public void reload() {
        Map<TagKey<Item>, HolderSet.Named<Item>> forge_tags = new HashMap<>();
        Map<TagKey<Item>, List<Holder<Item>>> vanilla_map = new HashMap<>();
        server_data.new_tags.forEach(tag -> {
            List<Holder<Item>> list = tag.content.stream().map(forge_item_registry::getHolder).map(Optional::orElseThrow).toList();

            HolderSet.Named<Item> set = new HolderSet.Named<>(vanilla_item_registry, tag.key);
            set.bind(list);
            forge_tags.put(tag.key, set);
            vanilla_map.put(tag.key, list);
        });
        forge_item_registry.live_edit_mixin_replaceTags(forge_tags);
        vanilla_item_registry.bindTags(vanilla_map);

        Ingredient.invalidateAll();

        vanilla_recipe_manager.replaceRecipes(server_data.new_recipes);
        PlayerList player_list = server.getPlayerList();
        player_list.saveAll();
        player_list.reloadResources();

        // TODO We need a custom packet to send that data to all players
        Map<Item, Integer> burn_time = new HashMap<>();
        server_data.new_burn_times.forEach(burn -> burn_time.put(burn.item(), burn.burn_time()));
        current_burn_times = burn_time;

        // TODO We need a custom packet to send that data to all players
        ComposterBlock.COMPOSTABLES.clear();
        server_data.new_compostables.forEach(compost -> ComposterBlock.COMPOSTABLES.put(compost.item(), compost.compastChance()));

        // TODO We need a custom packet to send that data to all players
        ((BrewingRecipeRegistryInterface)new BrewingRecipeRegistry()).live_edit_mixin_setRecipes(server_data.new_potions);

        server_data = null;
    }

    public void addNewRecipes(Collection<? extends Recipe<?>> recipes) {
        server_data.new_recipes.addAll(recipes);
    }

    public void addNewTags(Collection<Tag<Item>> tags) {
        server_data.new_tags.addAll(tags);
    }

    public void addNewBurnTimes(Collection<BurnTime> burnTimes) {
        server_data.new_burn_times.addAll(burnTimes);
    }

    public void addNewCompostables(Collection<CompostChance> compostables) {
        server_data.new_compostables.addAll(compostables);
    }

    public void addNewPotions(Collection<IBrewingRecipe> potions) {
        server_data.new_potions.addAll(potions);
    }

    private static class NewServerData {

        private final List<Recipe<?>> new_recipes = new ArrayList<>();
        private final List<Tag<Item>> new_tags = new ArrayList<>();
        private final List<BurnTime> new_burn_times = new ArrayList<>();
        private final List<CompostChance> new_compostables = new ArrayList<>();
        private final List<IBrewingRecipe> new_potions = new ArrayList<>();
    }
}
