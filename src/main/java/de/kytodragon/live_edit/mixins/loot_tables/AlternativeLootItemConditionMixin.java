package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CompositeLootItemCondition.class)
public interface AlternativeLootItemConditionMixin {

    @Accessor("terms")
    LootItemCondition[] live_edit_mixin_getTerms();
}
