package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InvertedLootItemCondition.class)
public interface InvertedLootItemConditionMixin {

    @Accessor("term")
    LootItemCondition live_edit_mixin_getTerm();
}
