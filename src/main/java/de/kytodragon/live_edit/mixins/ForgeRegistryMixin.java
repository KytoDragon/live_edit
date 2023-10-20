package de.kytodragon.live_edit.mixins;

import de.kytodragon.live_edit.mixin_interfaces.ForgeRegistryInterface;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Set;

@Mixin(ForgeRegistry.class)
public abstract class ForgeRegistryMixin<T> implements ForgeRegistryInterface<T> {

    @Shadow(remap = false)
    abstract void onBindTags(Map<TagKey<T>, HolderSet.Named<T>> tags, Set<TagKey<T>> defaultedTags);

    @Unique
    public void live_edit_mixin_replaceTags(Map<TagKey<T>, HolderSet.Named<T>> tags) {
        onBindTags(tags, null);
    }
}
