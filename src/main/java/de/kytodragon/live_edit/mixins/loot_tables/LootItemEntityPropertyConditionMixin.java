package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootItemEntityPropertyCondition.class)
public interface LootItemEntityPropertyConditionMixin {

    @Accessor("entityTarget")
    LootContext.EntityTarget live_edit_mixin_getTarget();

    @Accessor("predicate")
    EntityPredicate live_edit_mixin_getPredicate();
}
