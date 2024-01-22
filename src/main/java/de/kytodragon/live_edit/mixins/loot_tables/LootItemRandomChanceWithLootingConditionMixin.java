package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootItemRandomChanceWithLootingCondition.class)
public interface LootItemRandomChanceWithLootingConditionMixin {

    @Accessor("percent")
    float live_edit_mixin_getPercent();

    @Accessor("lootingMultiplier")
    float live_edit_mixin_getLootingMultiplier();
}
