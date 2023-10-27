package de.kytodragon.live_edit.recipe;

import java.util.HashMap;
import java.util.Map;

public record RecipeType(String name, net.minecraft.world.item.crafting.RecipeType<?> vanilla_type) {

    public static final Map<String, RecipeType> ALL_TYPES = new HashMap<>();

    public static final RecipeType CRAFTING = new RecipeType("CRAFTING", net.minecraft.world.item.crafting.RecipeType.CRAFTING);
    public static final RecipeType SMOKING = new RecipeType("SMOKING", net.minecraft.world.item.crafting.RecipeType.SMOKING);
    public static final RecipeType SMELTING = new RecipeType("SMELTING", net.minecraft.world.item.crafting.RecipeType.SMELTING);
    public static final RecipeType BLASTING = new RecipeType("BLASTING", net.minecraft.world.item.crafting.RecipeType.BLASTING);
    public static final RecipeType CAMPFIRE_COOKING = new RecipeType("CAMPFIRE_COOKING", net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING);
    public static final RecipeType STONECUTTING = new RecipeType("STONECUTTING", net.minecraft.world.item.crafting.RecipeType.STONECUTTING);
    public static final RecipeType SMITHING = new RecipeType("SMITHING", net.minecraft.world.item.crafting.RecipeType.SMITHING);
    public static final RecipeType TAGS = new RecipeType("TAGS", null);
    public static final RecipeType BURN_TIME = new RecipeType("BURN_TIME", null);
    public static final RecipeType BREWING = new RecipeType("BREWING", null);
    public static final RecipeType COMPOSTING = new RecipeType("COMPOSTING", null);

    /** Dummy type used to refer to all recipe types */
    public static final RecipeType ALL = new RecipeType("ALL", null);

    public RecipeType(String name, net.minecraft.world.item.crafting.RecipeType<?> vanilla_type) {
        this.name = name;
        this.vanilla_type = vanilla_type;
        if (!"Dummy".equals(name) && ALL_TYPES.put(name, this) != null) {
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

        if (vanilla_type != null)
            return vanilla_type == t.vanilla_type;
        else
            return name.equals(t.name);
    }

    @Override
    public int hashCode() {
        if (vanilla_type != null)
            return vanilla_type.hashCode();
        else
            return name.hashCode();
    }
}
