package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.advancements.critereon.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPredicate.class)
public interface EntityPredicateMixin {

    @Accessor("entityType")
    EntityTypePredicate live_edit_mixin_getEntityType();
    @Accessor("distanceToPlayer")
    DistancePredicate live_edit_mixin_getDistanceToPlayer();
    @Accessor("location")
    LocationPredicate live_edit_mixin_getLocation();
    @Accessor("steppingOnLocation")
    LocationPredicate live_edit_mixin_getSteppingOnLocation();
    @Accessor("effects")
    MobEffectsPredicate live_edit_mixin_getEffects();
    @Accessor("nbt")
    NbtPredicate live_edit_mixin_getNbt();
    @Accessor("flags")
    EntityFlagsPredicate live_edit_mixin_getFlags();
    @Accessor("equipment")
    EntityEquipmentPredicate live_edit_mixin_getEquipment();
    @Accessor("subPredicate")
    EntitySubPredicate live_edit_mixin_getSubPredicate();
    @Accessor("vehicle")
    EntityPredicate live_edit_mixin_getVehicle();
    @Accessor("passenger")
    EntityPredicate live_edit_mixin_getPassenger();
    @Accessor("targetedEntity")
    EntityPredicate live_edit_mixin_getTargetedEntity();
    @Accessor("team")
    String live_edit_mixin_getTeam();
}
