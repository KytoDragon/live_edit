package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFlagsPredicate.class)
public interface EntityFlagsPredicateMixin {

    @Accessor("isOnFire")
    Boolean live_edit_mixin_getIsOnFire();
    @Accessor("isCrouching")
    Boolean live_edit_mixin_getIsCrouching();
    @Accessor("isSprinting")
    Boolean live_edit_mixin_getIsSprinting();
    @Accessor("isSwimming")
    Boolean live_edit_mixin_getIsSwimming();
    @Accessor("isBaby")
    Boolean live_edit_mixin_getIsBaby();
}
