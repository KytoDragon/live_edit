package de.kytodragon.live_edit.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LootTables.class)
public interface LootTablesMixin {

    @Accessor("tables")
    Map<ResourceLocation, LootTable> live_edit_mixin_getTables();

    @Accessor("tables")
    void live_edit_mixin_setTables(Map<ResourceLocation, LootTable> tables);
}
