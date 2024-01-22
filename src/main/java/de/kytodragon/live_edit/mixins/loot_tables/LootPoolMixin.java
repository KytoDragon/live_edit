package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootPool.class)
public interface LootPoolMixin {

    @Accessor("entries")
    LootPoolEntryContainer[] live_edit_mixin_getEntries();
    @Accessor("conditions")
    LootItemCondition[] live_edit_mixin_getConditions();
    @Accessor("functions")
    LootItemFunction[] live_edit_mixin_getFunctions();

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static LootPool create(LootPoolEntryContainer[] entries, LootItemCondition[] conditions, LootItemFunction[] functions, NumberProvider rolls, NumberProvider bonusRolls, String name) {
        return null;
    }
}
