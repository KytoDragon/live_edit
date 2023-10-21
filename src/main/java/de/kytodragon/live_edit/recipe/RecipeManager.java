package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.mixin_interfaces.ForgeRegistryInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.*;

import static de.kytodragon.live_edit.LiveEditMod.LOGGER;

public class RecipeManager {

    public static RecipeManager instance = new RecipeManager();

    public HashMap<RecipeType, IRecipeManipulator<ResourceLocation, ? extends Recipe<?>>> manipulators = new HashMap<>();
    public GeneralManipulationData data = new GeneralManipulationData();

    public void markRecipeForDeletion(RecipeType type, ResourceLocation recipeKey) {
        manipulators.get(type).markRecipeForDeletion(recipeKey);
    }

    public void markItemForReplacement(Item item, Item replacement) {
        data.itemsToReplace.put(item, replacement);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void manipulateAllItemTags() {
        ITagManager<Item> manager = ForgeRegistries.ITEMS.tags();
        Objects.requireNonNull(manager);

        Map<TagKey<Item>, HolderSet.Named<Item>> forge_tags = new HashMap<>();
        Map<TagKey<Item>, List<Holder<Item>>> vanilla_map = new HashMap<>();
        manager.getTagNames().forEach(key -> {
            List<Holder<Item>> list = manager.getTag(key).stream()
                    .map(item -> data.itemsToReplace.getOrDefault(item, item))
                    .map(ForgeRegistries.ITEMS::getHolder).map(Optional::orElseThrow)
                    .toList();
            HolderSet.Named<Item> set = new HolderSet.Named<>(Registry.ITEM, key);
            set.bind(list);
            forge_tags.put(key, set);
            vanilla_map.put(key, list);
        });
        ((ForgeRegistryInterface<Item>) ForgeRegistries.ITEMS).live_edit_mixin_replaceTags(forge_tags);
        Registry.ITEM.bindTags(vanilla_map);
    }

    public List<Recipe<?>> manipulateAllRecipes(MinecraftServer server) {

        // Deal with recipe types in the standard recipe manager that are not beeing handled by manipulator.
        // THis makes shure we do not delete recipes we do not know about.
        for (net.minecraft.world.item.crafting.RecipeType<?> recipeType : ForgeRegistries.RECIPE_TYPES.getValues()) {
            if (manipulators.keySet().stream().noneMatch(s -> s.vanilla_type() == recipeType)) {
                manipulators.put(new RecipeType("Dummy", recipeType), getDummyManipulator(recipeType));
            }
        }

        Ingredient.invalidateAll();

        List<Recipe<?>> all_new_recipes = new ArrayList<>();
        try {

            for (IRecipeManipulator<?, ? extends Recipe<?>> manipulator : manipulators.values()) {
                all_new_recipes.addAll(genericsHelper(server, manipulator));
            }

        } catch (Exception e) {
            LOGGER.info("Failed to replace recipes: ", e);
        }
        return  all_new_recipes;
    }

    private <R> List<R> genericsHelper(MinecraftServer server, IRecipeManipulator<?, R> manipulator) {
        Collection<R> old_recipes = manipulator.getCurrentRecipes(server.getRecipeManager());
        return manipulator.manipulateRecipes(old_recipes, data);
    }

    private <R extends Recipe<?>> IRecipeManipulator<ResourceLocation, R> getDummyManipulator(net.minecraft.world.item.crafting.RecipeType<R> vanillaType) {

        return new IRecipeManipulator<>() {
            @Override
            public ResourceLocation getKey(R recipe) {
                return recipe.getId();
            }

            @Override
            public R manipulate(R recipe, GeneralManipulationData data) {
                return recipe;
            }

            @Override
            public Collection<R> getCurrentRecipes(net.minecraft.world.item.crafting.RecipeManager manager) {
                return genericsHelper(manager);
            }

            @Override
            public boolean isRealImplementation() {
                return false;
            }

            @Override
            public Optional<R> getRecipe(net.minecraft.world.item.crafting.RecipeManager manager, ResourceLocation key) {
                throw new UnsupportedOperationException();
            }

            @SuppressWarnings("unchecked")
            private  <C extends Container> Collection<R> genericsHelper(net.minecraft.world.item.crafting.RecipeManager manager) {
                net.minecraft.world.item.crafting.RecipeType<? extends Recipe<C>> type = (net.minecraft.world.item.crafting.RecipeType<? extends Recipe<C>>) vanillaType;
                return (Collection<R>) manager.getAllRecipesFor(type);
            }
        };
    }
}
