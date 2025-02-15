package de.kytodragon.live_edit.recipe;

import java.util.HashMap;
import java.util.Map;

public record RecipeType(String name, String crafttweaker_name) {

    public static final Map<String, RecipeType> ALL_TYPES = new HashMap<>();

    public static final RecipeType CRAFTING = new RecipeType("CRAFTING", "minecraft:crafting");
    public static final RecipeType SMOKING = new RecipeType("SMOKING", "minecraft:smoking");
    public static final RecipeType SMELTING = new RecipeType("SMELTING", "minecraft:smelting");
    public static final RecipeType BLASTING = new RecipeType("BLASTING", "minecraft:blasting");
    public static final RecipeType CAMPFIRE_COOKING = new RecipeType("CAMPFIRE_COOKING", "minecraft:campfire_cooking");
    public static final RecipeType STONECUTTING = new RecipeType("STONECUTTING", "minecraft:stonecutting");
    public static final RecipeType SMITHING = new RecipeType("SMITHING", "minecraft:smithing");
    public static final RecipeType TAGS = new RecipeType("TAGS", null);
    public static final RecipeType BURN_TIME = new RecipeType("BURN_TIME", null);
    public static final RecipeType BREWING = new RecipeType("BREWING", null);
    public static final RecipeType COMPOSTING = new RecipeType("COMPOSTING", null);
    public static final RecipeType LOOT_TABLE = new RecipeType("LOOT_TABLE", null);

    /** Dummy type used to refer to all recipe types */
    public static final RecipeType ALL = new RecipeType("ALL", null);

    public RecipeType(String name, String crafttweaker_name) {
        this.name = name;
        this.crafttweaker_name = crafttweaker_name;
        if (ALL_TYPES.put(name, this) != null) {
            throw new IllegalStateException("Duplicate recipe type " + name);
        }
    }

    /**
     * Two types with the same vanilla type override each other in order to replace the dummy-implementations set at startup.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof RecipeType t))
            return false;

        return name.equals(t.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
