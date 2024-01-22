package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPoolEntryContainer.class)
public interface LootPoolEntryContainerMixin {

    @Accessor("conditions")
    LootItemCondition[] live_edit_mixin_getConditions();
}
