package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AlternativesEntry.class)
public interface AlternativesEntryMixin {

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static AlternativesEntry create(LootPoolEntryContainer[] entries, LootItemCondition[] conditions) {
        return null;
    }
}
