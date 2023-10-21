package de.kytodragon.live_edit.integration.vanilla;

import net.minecraft.tags.TagKey;

import java.util.Set;

public class Tag<T> {

    public TagKey<T> key;

    public Set<T> content;
}
