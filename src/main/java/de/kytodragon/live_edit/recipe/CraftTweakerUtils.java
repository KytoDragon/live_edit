package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.editing.MyIngredient;

import java.util.List;

public class CraftTweakerUtils {

    public static void exportIngredients(StringBuilder sb, List<MyIngredient> ingredients) {

        sb.append("[");
        boolean first = true;
        for (MyIngredient ingredient : ingredients) {
            if (!first)
                sb.append(", ");
            first = false;
            ingredient.export(sb);
        }
        sb.append("]");
    }

    public static void exportRecipeType(StringBuilder sb, RecipeType type) {
        sb.append("<recipetype:");
        sb.append(type.crafttweaker_name());
        sb.append(">");
    }
}
