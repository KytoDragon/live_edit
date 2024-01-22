package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryMixin {

    @Accessor("tag")
    TagKey<Item> live_edit_mixin_getTag();

    @Accessor("expand")
    boolean live_edit_mixin_getExpand();
}
