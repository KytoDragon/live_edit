package de.kytodragon.live_edit.mixins.loot_tables;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformGenerator.class)
public interface UniformGeneratorMixin {

    @Accessor("min")
    NumberProvider live_edit_mixin_getMin();

    @Accessor("max")
    NumberProvider live_edit_mixin_getMax();
}
