package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootItemRandomChanceCondition.class)
public interface LootItemRandomChanceConditionMixin {

    @Accessor("probability")
    float live_edit_mixin_getProbability();
}
