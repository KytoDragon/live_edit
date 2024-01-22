package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CompositeEntryBase.class)
public interface CompositeEntryBaseMixin {

    @Accessor("children")
    LootPoolEntryContainer[] live_edit_mixin_getChildren();
}
