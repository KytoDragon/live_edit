package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SlimePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlimePredicate.class)
public interface SlimePredicateMixin {

    @Accessor("size")
    MinMaxBounds.Ints live_edit_mixin_getSize();
}
