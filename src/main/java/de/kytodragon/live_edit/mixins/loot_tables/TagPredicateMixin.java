package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.advancements.critereon.EntityTypePredicate$TagPredicate")
public interface TagPredicateMixin {

    @Accessor("tag")
    TagKey<EntityType<?>> live_edit_mixin_getTag();
}
