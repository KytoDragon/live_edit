package de.kytodragon.live_edit.mixins;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SequentialEntry.class)
public interface SequentialEntryMixin {

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static SequentialEntry create(LootPoolEntryContainer[] entries, LootItemCondition[] conditions) {
        return null;
    }
}
