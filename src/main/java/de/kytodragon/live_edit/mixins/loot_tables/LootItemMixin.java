package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootItem.class)
public interface LootItemMixin {

    @Accessor("item")
    Item live_edit_mixin_getItem();

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static LootItem create(Item item, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
        return null;
    }
}
