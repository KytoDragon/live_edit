package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.advancements.critereon.EntityTypePredicate$TypePredicate")
public interface TypePredicateMixin {

    @Accessor("type")
    EntityType<?> live_edit_mixin_getType();
}
