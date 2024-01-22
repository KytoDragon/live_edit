package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AlternativeLootItemCondition.class)
public interface AlternativeLootItemConditionMixin {

    @Accessor("terms")
    LootItemCondition[] live_edit_mixin_getTerms();
}
