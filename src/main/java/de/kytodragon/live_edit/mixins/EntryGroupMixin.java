package de.kytodragon.live_edit.mixins;

import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryGroup.class)
public interface EntryGroupMixin {

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static EntryGroup create(LootPoolEntryContainer[] entries, LootItemCondition[] conditions) {
        return null;
    }
}
