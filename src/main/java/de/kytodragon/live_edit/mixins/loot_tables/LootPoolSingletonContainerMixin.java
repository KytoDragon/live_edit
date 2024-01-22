package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPoolSingletonContainer.class)
public interface LootPoolSingletonContainerMixin {

    @Accessor("weight")
    int live_edit_mixin_getWeight();
    @Accessor("quality")
    int live_edit_mixin_getQuality();
    @Accessor("functions")
    LootItemFunction[] live_edit_mixin_getFunctions();
}
