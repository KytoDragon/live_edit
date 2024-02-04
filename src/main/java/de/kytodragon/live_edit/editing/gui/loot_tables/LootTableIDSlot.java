package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootTable;
import de.kytodragon.live_edit.integration.vanilla.LootTableManipulator;
import de.kytodragon.live_edit.recipe.LootTableConverter;
import de.kytodragon.live_edit.recipe.RecipeManager;
import de.kytodragon.live_edit.recipe.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class LootTableIDSlot extends Slot {

    public ResourceLocation id;

    public LootTableIDSlot(int slot_id) {
        super(new RecipeWrapper(null), slot_id, 5000, 5000);
    }

    public MyLootTable getLootTable() {
        return genericsHelper((LootTableManipulator) RecipeManager.instance.manipulators.get(RecipeType.LOOT_TABLE));
    }

    private MyLootTable genericsHelper(LootTableManipulator manipulator) {
        Optional<LootTable> loot_table = manipulator.getRecipe(id);
        if (loot_table.isEmpty())
            return null;

        return LootTableConverter.convertLootTable(loot_table.get());
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
        if (id != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("loot_table_id", id.toString());
            item.setTag(tag);
        }
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
            if (tag.get("loot_table_id") != null) {
                id = ResourceLocation.of(tag.getString("loot_table_id"), ':');
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
