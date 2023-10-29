package de.kytodragon.live_edit.editing.gui;

import de.kytodragon.live_edit.editing.MyRecipe;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class RecipeIDSlot extends Slot {

    public RecipeType type;
    public ResourceLocation id;

    public RecipeIDSlot(int slot_id) {
        super(new RecipeWrapper(null), slot_id, 5000, 5000);
    }

    public MyRecipe getRecipe() {
        return genericsHelper(RecipeManager.instance.manipulators.get(type));
    }

    private <T> MyRecipe genericsHelper(IRecipeManipulator<ResourceLocation, T, ?> manipulator) {
        if (manipulator == null)
            return null;

        Optional<T> recipe = manipulator.getRecipe(id);
        if (recipe.isEmpty())
            return null;

        return manipulator.encodeRecipe(recipe.get());
    }

    @Override
    public void onTake(Player p_150645_, ItemStack item) {
        this.setChanged();
    }

    @Override
    public boolean mayPlace(ItemStack item) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Items.ANDESITE);
        CompoundTag tag = new CompoundTag();
        tag.putString("recipe_type", type.name());
        tag.putString("recipe_id", id.toString());
        item.setTag(tag);
        return item;
    }

    @Override
    public boolean hasItem() {
        return true;
    }

    @Override
    public void set(ItemStack item) {
    }

    @Override
    public void initialize(ItemStack item) {
        if (item.getTag() != null) {
            CompoundTag tag = item.getTag();
            if (tag.get("recipe_type") != null && tag.get("recipe_id") != null) {
                type = RecipeType.ALL_TYPES.get(tag.getString("recipe_type"));
                id = ResourceLocation.of(tag.getString("recipe_id"), ':');
            }
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }


    @Override
    public ItemStack remove(int p_40227_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player p_40228_) {
        return false;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return false;
    }

    @Override
    public Optional<ItemStack> tryRemove(int p_150642_, int p_150643_, Player p_150644_) {
        return Optional.empty();
    }

    @Override
    public ItemStack safeTake(int p_150648_, int p_150649_, Player p_150650_) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack safeInsert(ItemStack p_150657_, int p_150658_) {
        return p_150657_;
    }

    @Override
    public boolean allowModification(Player p_150652_) {
        return false;
    }
}
