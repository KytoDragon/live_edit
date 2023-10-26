package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.editing.MyIngredient;
import de.kytodragon.live_edit.mixins.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class IngredientReplacer {

    public static boolean isToReplace(ItemStack stack, GeneralManipulationData data) {
        return data.itemsToReplace.containsKey(stack.getItem());
    }

    public static ItemStack replace(ItemStack stack, GeneralManipulationData data) {
        Item replacement = data.itemsToReplace.get(stack.getItem());
        if (replacement == null) {
            return stack;
        }
        return new ItemStack(replacement, stack.getCount());
    }

    public static boolean isToReplace(NonNullList<Ingredient> ingredients, GeneralManipulationData data) {
        return ingredients.stream().flatMap(IngredientReplacer::streamIngredients)
                .filter(s -> s instanceof Ingredient.ItemValue)
                .map(Ingredient.Value::getItems)
                .flatMap(Collection::stream)
                .anyMatch(s -> isToReplace(s, data));
    }

    public static boolean isToReplace(Ingredient ingredient, GeneralManipulationData data) {
        return IngredientReplacer.streamIngredients(ingredient)
                .filter(s -> s instanceof Ingredient.ItemValue)
                .map(Ingredient.Value::getItems)
                .flatMap(Collection::stream)
                .anyMatch(s -> isToReplace(s, data));
    }

    private static Stream<Ingredient.Value> streamIngredients(Ingredient ingredient) {
        ingredient.checkInvalidation();
        if (ingredient instanceof AbstractIngredient) {
            if (ingredient instanceof IntersectionIngredient intersection) {
                return ((IntersectionIngredientMixin)intersection).live_edit_mixin_getChildren().stream().flatMap(IngredientReplacer::streamIngredients);
            } else if (ingredient instanceof DifferenceIngredient difference) {
                DifferenceIngredientMixin diff = (DifferenceIngredientMixin)difference;
                return Stream.concat(streamIngredients(diff.live_edit_mixin_getBase()), streamIngredients(diff.live_edit_mixin_getSubtracted()));
            } else if (ingredient instanceof CompoundIngredient compound) {
                return ((CompoundIngredientMixin)compound).live_edit_mixin_getChildren().stream().flatMap(IngredientReplacer::streamIngredients);
            } else{
                // TODO we do not check ingredients with NBT-Matching at this moment
                return Stream.of();
            }
        } else {
            return Arrays.stream(((IngredientMixin)ingredient).live_edit_mixin_getRawIngrediants());
        }
    }

    public static NonNullList<Ingredient> replace(NonNullList<Ingredient> ingredients, GeneralManipulationData data) {
        NonNullList<Ingredient> new_ingredients = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        int i = 0;
        for (Ingredient ingredient : ingredients) {
            new_ingredients.set(i++, replace(ingredient, data));
        }
        return new_ingredients;
    }

    public static Ingredient replace(Ingredient ingredient, GeneralManipulationData data) {
        if (ingredient instanceof AbstractIngredient) {
            if (ingredient instanceof IntersectionIngredient intersection) {
                return IntersectionIngredient.of(((CompoundIngredientMixin)intersection).live_edit_mixin_getChildren().stream()
                        .map(s -> replace(s, data)).toArray(Ingredient[]::new));
            } else if (ingredient instanceof DifferenceIngredient difference) {
                DifferenceIngredientMixin diff = (DifferenceIngredientMixin)difference;
                return DifferenceIngredient.of(replace(diff.live_edit_mixin_getBase(), data), replace(diff.live_edit_mixin_getSubtracted(), data));
            } else if (ingredient instanceof CompoundIngredient compound) {
                return CompoundIngredient.of(((CompoundIngredientMixin)compound).live_edit_mixin_getChildren().stream()
                        .map(s -> replace(s, data)).toArray(Ingredient[]::new));
            } else{
                // TODO we do not check ingredients with NBT-Matching at this moment
                return ingredient;
            }
        } else {
            return Ingredient.fromValues(Arrays.stream(((IngredientMixin) ingredient).live_edit_mixin_getRawIngrediants()).map(s -> replace(s, data)));
        }
    }

    private static Ingredient.Value replace(Ingredient.Value value, GeneralManipulationData data) {
        if (value instanceof Ingredient.ItemValue itemValue) {
            return new Ingredient.ItemValue(replace(itemValue.getItems().stream().findAny().orElseThrow(), data));
        }
        // Don't need to replace tags
        return value;
    }

    @Nullable
    public static List<MyIngredient> encodeIngredients(NonNullList<Ingredient> ingredients) {
        List<MyIngredient> result = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            MyIngredient myIngredient = encodeIngredient(ingredient);
            if (myIngredient == null)
                return null;
            result.add(myIngredient);
        }
        return result;
    }

    @Nullable
    public static MyIngredient encodeIngredient(Ingredient ingredient) {
        if (ingredient instanceof AbstractIngredient)
            return null;

        Ingredient.Value[] values = ((IngredientMixin) ingredient).live_edit_mixin_getRawIngrediants();
        if (values.length != 1)
            return null;

        Ingredient.Value value = values[0];
        if (value instanceof Ingredient.ItemValue itemValue) {
            return new MyIngredient.ItemIngredient(itemValue.getItems().stream().findAny().orElseThrow());
        } else if (value instanceof Ingredient.TagValue tagValue) {
            return new MyIngredient.TagIngredient(((IngredientTagValueMixin)tagValue).live_edit_mixin_getTag());
        } else {
            // no item lists for now
            return null;
        }
    }

    public static NonNullList<Ingredient> decodeIngredients(List<MyIngredient> ingredients) {
        return decodeIngredients(ingredients, ingredients.size());
    }

    public static NonNullList<Ingredient> decodeIngredients(List<MyIngredient> ingredients, int max_ingredients) {
        if (max_ingredients < 0)
            max_ingredients = ingredients.size() + max_ingredients;
        NonNullList<Ingredient> result = NonNullList.withSize(max_ingredients, Ingredient.EMPTY);
        for (int i = 0; i < max_ingredients; i++) {
            result.set(i++, decodeIngredient(ingredients.get(i)));
        }
        return result;
    }

    public static Ingredient decodeIngredient(MyIngredient ingredient) {
        if (ingredient instanceof MyIngredient.ItemIngredient itemIngredient) {
            return Ingredient.of(itemIngredient.item);
        } else if (ingredient instanceof MyIngredient.TagIngredient tagIngredient) {
            if (tagIngredient.tag_amount != 1)
                throw new IllegalStateException("tag with amount != 1");
            return Ingredient.of(tagIngredient.tag);
        } else {
            throw new IllegalStateException("illegal ingredient type for base crafting recipe");
        }
    }
}
