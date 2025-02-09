package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LootTable.class)
public interface LootTableMixin {

    @Accessor("pools")
    List<LootPool> live_edit_mixin_getPools();
    @Accessor("functions")
    LootItemFunction[] live_edit_mixin_getFunctions();

    @SuppressWarnings("SameReturnValue")
    @Invoker("<init>")
    static LootTable create(LootContextParamSet paramSet, ResourceLocation rl, LootPool[] pools, LootItemFunction[] functions) {
        return null;
    }
}
