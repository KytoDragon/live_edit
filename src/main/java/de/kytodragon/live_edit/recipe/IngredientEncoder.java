package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.editing.MyIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IngredientEncoder {

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

        Ingredient.Value[] values = ingredient.values;
        if (values.length == 0)
            return new MyIngredient.ItemIngredient(Items.AIR); // Dummy item for shaped crafting

        if (values.length > 1) {
            MyIngredient.ItemListIngredient item_list = new MyIngredient.ItemListIngredient();
            for (Ingredient.Value value : values) {
                if (value instanceof Ingredient.ItemValue itemValue) {
                    item_list.item_list.addAll(itemValue.getItems());
                } else {
                    return null;
                }
            }
            return item_list;
        }

        Ingredient.Value value = values[0];
        if (value instanceof Ingredient.ItemValue itemValue) {
            return new MyIngredient.ItemIngredient(itemValue.getItems().stream().findAny().orElseThrow());
        } else if (value instanceof Ingredient.TagValue tagValue) {
            return new MyIngredient.TagIngredient(tagValue.tag);
        } else {
            return null;
        }
    }
}
