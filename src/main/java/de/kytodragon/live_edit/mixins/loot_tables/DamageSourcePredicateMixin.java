package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageSourcePredicate.class)
public interface DamageSourcePredicateMixin {

    @Accessor("isProjectile")
	Boolean live_edit_mixin_getIsProjectile();
    @Accessor("isExplosion")
	Boolean live_edit_mixin_getIsExplosion();
    @Accessor("bypassesArmor")
	Boolean live_edit_mixin_getBypassesArmor();
    @Accessor("bypassesInvulnerability")
	Boolean live_edit_mixin_getBypassesInvulnerability();
    @Accessor("bypassesMagic")
	Boolean live_edit_mixin_getBypassesMagic();
    @Accessor("isFire")
	Boolean live_edit_mixin_getIsFire();
    @Accessor("isMagic")
	Boolean live_edit_mixin_getIsMagic();
    @Accessor("isLightning")
	Boolean live_edit_mixin_getIsLightning();
    @Accessor("directEntity")
	EntityPredicate live_edit_mixin_getDirectEntity();
    @Accessor("sourceEntity")
	EntityPredicate live_edit_mixin_getSourceEntity();
}
